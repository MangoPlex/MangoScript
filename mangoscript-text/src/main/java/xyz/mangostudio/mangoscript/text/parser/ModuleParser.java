package xyz.mangostudio.mangoscript.text.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.func.GuestFunction;
import xyz.mangostudio.mangoscript.binary.module.MangoScriptModule;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.binary.type.oop.ClassType;
import xyz.mangostudio.mangoscript.binary.type.oop.ObjectClass;
import xyz.mangostudio.mangoscript.text.lexer.Lexer;
import xyz.mangostudio.mangoscript.text.lexer.LexerException;
import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.StringStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.DynamicTokenStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.LiteralString;
import xyz.mangostudio.mangoscript.text.lexer.token.Symbol;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;

public class ModuleParser {
	/**
	 * <p>
	 * A simple to use parse method for parsing MangoScript modules from
	 * {@link Reader}. This will setup a basic parsing pipeline and parse the code
	 * to {@link MangoScriptModule}.
	 * </p>
	 * <p>
	 * <b>Pipeline</b>: {@link StringStream} -> {@link Lexer} -> {@link TokenStream}
	 * -> {@link #parse(TokenStream, Parser)}.
	 * </p>
	 * <p>
	 * You can make your own pipeline, such as providing your own token from
	 * somewhere else (eg: binary source).
	 * </p>
	 * 
	 * @param reader The reader tha yields out characters.
	 * @return A parsed module.
	 * @throws LexerException  If an error occurred while applying lexer to given
	 *                         code, such as invalid token.
	 * @throws ParserException If an error occurred during parsing phase, such as
	 *                         invalid tokens placement.
	 */
	public static MangoScriptModule parse(Reader reader) {
		ReaderStringStream string = new ReaderStringStream(reader);
		StringStreamLexer lexer = new StringStreamLexer(Token.FACTORY, string);
		DynamicTokenStream token = new DynamicTokenStream(lexer::nextToken);
		return parse(token, Parsers.ALL);
	}

	public static MangoScriptModule parse(TokenStream tokens, Parser parser) {
		MangoScriptModule module = new MangoScriptModule();

		while (tokens.hasNext()) {
			if (parseImport(tokens, module) ||
				parseExport(tokens, module) ||
				parseClass(tokens, module, parser) ||
				parseFunction(tokens, module, parser)) continue;

			throw new ParserException("Expected Import, Class, Function or Export, but " + tokens.getAhead()
				+ " found");
		}

		return module;
	}

	public static boolean parseImport(TokenStream tokens, MangoScriptModule module) {
		if (tokens.getAhead() == Keyword.IMPORT) {
			tokens.skipToken();

			if (!(tokens.getAhead() instanceof LiteralString path))
				throw new ParserException("Expected String, but " + tokens.getAhead() + " found");
			tokens.skipToken();

			if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected ';', but " + tokens.getAhead() + " found");
			tokens.skipToken();

			module.withImport(path.content());
			return true;
		}

		return false;
	}

	public static boolean parseExport(TokenStream tokens, MangoScriptModule module) {
		if (tokens.getAhead() == Keyword.EXPORT) {
			tokens.skipToken();

			if (!(tokens.getAhead() instanceof Symbol target))
				throw new ParserException("Expected Symbol, but " + tokens.getAhead() + " found");
			tokens.skipToken();

			String exportAs;
			if (tokens.getAhead() == Keyword.AS) {
				tokens.skipToken();

				if (!(tokens.getAhead() instanceof Symbol exportAsSymbol))
					throw new ParserException("Expected Symbol, but " + tokens.getAhead() + " found");
				tokens.skipToken();

				exportAs = exportAsSymbol.name();
			} else {
				exportAs = target.name();
			}

			if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected ';', but " + tokens.getAhead() + " found");
			tokens.skipToken();
			module.withExport(target.name(), exportAs);
			return true;
		}

		return false;
	}

	public static boolean parseClass(TokenStream tokens, MangoScriptModule module, Parser parser) {
		ObjectClass clazz = parseClass(tokens, parser);

		if (clazz != null) {
			module.withClass(clazz.getName(), clazz);
			return true;
		}

		return false;
	}

	public static ObjectClass parseClass(TokenStream tokens, Parser parser) {
		Token token = tokens.getAhead();
		if (token != Keyword.CLASS) return null;
		tokens.skipToken();

		if (!((token = tokens.getAhead()) instanceof Symbol className))
			throw new ParserException("Expected Symbol, but " + token + " found");
		tokens.skipToken();

		Type superclass;
		if (tokens.getAhead() == SymbolicKeyword.OPEN_CURLY_BRACKET) {
			superclass = new ClassType("object");
			tokens.skipToken();
		} else if (tokens.getAhead() == Keyword.EXTENDS) {
			tokens.skipToken();

			superclass = TypeParser.parseBaseType(tokens);
			if (superclass == null)
				throw new ParserException("Expected valid base type, but " + tokens.getAhead() + " found");

			if (tokens.getAhead() != SymbolicKeyword.OPEN_CURLY_BRACKET)
				throw new ParserException("Expected '{', but " + tokens.getAhead() + " found");
			tokens.skipToken();
		} else {
			throw new ParserException("Expected '{' or extends, but " + tokens.getAhead() + " found");
		}

		if (!(superclass instanceof ClassType superclassRef))
			throw new ParserException("Expected class reference, but " + superclass + " found");

		ObjectClass clazz = new ObjectClass(className.name(), superclassRef);

		while ((token = tokens.getAhead()) != SymbolicKeyword.CLOSE_CURLY_BRACKET) {
			if (!tokens.hasNext()) throw new ParserException("Expected '}', but end of stream found");
			parseClassEntry(clazz, tokens, parser);
		}

		tokens.skipToken();
		return clazz;
	}

	private static boolean isOperator(SymbolicKeyword keyword) {
		return switch (keyword) {
		case PLUS, MINUS, MULTIPLY, DIVIDE, POWER, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUALS,
			LESS_THAN_OR_EQUALS, NOT, INCREMENT, DECREMENT -> true;
		default -> false;
		};
	}

	private static void parseClassEntry(ObjectClass clazz, TokenStream tokens, Parser parser) {
		Type type = TypeParser.parseType(tokens);
		if (type == null) throw new ParserException("Expected a valid type, but " + tokens.getAhead() + " found");

		if (tokens.getAhead() instanceof SymbolicKeyword keyword && isOperator(keyword)) {
			// Overloading
			tokens.skipToken();

			if (tokens.getAhead() != SymbolicKeyword.OPEN_PARENTHESES)
				throw new ParserException("Expected '(', but " + tokens.getAhead() + " found");
			tokens.skipToken();

			boolean isUnary = keyword == SymbolicKeyword.NOT
				|| keyword == SymbolicKeyword.INCREMENT
				|| keyword == SymbolicKeyword.DECREMENT;
			Type argType = null;
			String argName = null;

			if (!isUnary) {
				argType = TypeParser.parseType(tokens);
				if (argType == null)
					throw new ParserException("Expected a valid type, but " + tokens.getAhead() + " found");

				if (!(tokens.getAhead() instanceof Symbol argNameSymbol))
					throw new ParserException("Expected Symbol, but " + tokens.getAhead() + " found");
				tokens.skipToken();
				argName = argNameSymbol.name();
			}

			if (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES)
				throw new ParserException("Expected ')', but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Statement s = parser.parseStatement(tokens);
			if (s == null) throw new ParserException("Expected Statement, but " + tokens.getAhead() + " found");

			FunctionSignature functionType = isUnary ? new FunctionSignature(type)
				: new FunctionSignature(type, argType);
			String[] argNames = isUnary ? new String[0] : new String[] { argName };
			clazz.defineFunction(keyword.getKeyword(), functionType, argNames, s);
			return;
		}

		if (!(tokens.getAhead() instanceof Symbol symbol))
			throw new ParserException("Expected Symbol, but " + tokens.getAhead() + " found");
		tokens.skipToken();

		if (tokens.getAhead() == SymbolicKeyword.SEMICOLON) {
			tokens.skipToken();
			clazz.defineField(symbol.name(), type);
			return;
		}

		if (tokens.getAhead() == SymbolicKeyword.OPEN_PARENTHESES) {
			tokens.skipToken();
			List<Type> argTypes = new ArrayList<>();
			List<String> argNames = new ArrayList<>();

			while (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES) {
				Type argType = TypeParser.parseType(tokens);
				if (argType == null)
					throw new ParserException("Expected a valid type, but " + tokens.getAhead() + " found");

				if (!(tokens.getAhead() instanceof Symbol argNameSym))
					throw new ParserException("Expected Symbol but " + tokens.getAhead() + " found");
				tokens.skipToken();
				argTypes.add(argType);
				argNames.add(argNameSym.name());
				if (tokens.getAhead() == SymbolicKeyword.CLOSE_PARENTHESES) break;
				if (tokens.getAhead() != SymbolicKeyword.COMMA)
					throw new ParserException("Expected ',' or ')', but " + tokens.getAhead() + " found");
				tokens.skipToken();
			}

			tokens.skipToken();

			Statement s = parser.parseStatement(tokens);
			if (s == null) throw new ParserException("Expected Statement, but " + tokens.getAhead() + " found");

			FunctionSignature functionType = new FunctionSignature(type, argTypes.toArray(Type[]::new));
			clazz.defineFunction(symbol.name(), functionType, argNames.toArray(String[]::new), s);
			return;
		}

		throw new ParserException("Expected ';' or '(', but " + tokens.getAhead() + " found");
	}

	public static boolean parseFunction(TokenStream tokens, MangoScriptModule module, Parser parser) {
		GuestFunction function = parseFunction(tokens, parser);

		if (function != null) {
			module.withFunction(function.getName(), function);
			return true;
		}

		return false;
	}

	public static GuestFunction parseFunction(TokenStream tokens, Parser parser) {
		tokens = tokens.fork();

		Type type = TypeParser.parseType(tokens);
		if (type == null) {
			tokens.destroy();
			return null;
		}

		if (!(tokens.getAhead() instanceof Symbol symbol)) {
			tokens.destroy();
			return null;
		}

		tokens.skipToken();

		if (tokens.getAhead() != SymbolicKeyword.OPEN_PARENTHESES) {
			tokens.destroy();
			return null;
		}

		tokens.skipToken();

		// 100% confirmed we are parsing function
		List<Type> argTypes = new ArrayList<>();
		List<String> argNames = new ArrayList<>();

		while (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES) {
			Type argType = TypeParser.parseType(tokens);
			if (argType == null) {
				Token t = tokens.getAhead();
				tokens.destroy();
				throw new ParserException("Expected a valid type, but " + t + " found");
			}

			if (!(tokens.getAhead() instanceof Symbol argNameSym)) {
				Token t = tokens.getAhead();
				tokens.destroy();
				throw new ParserException("Expected Symbol but " + t + " found");
			}

			tokens.skipToken();
			argTypes.add(argType);
			argNames.add(argNameSym.name());
			if (tokens.getAhead() == SymbolicKeyword.CLOSE_PARENTHESES) break;
			if (tokens.getAhead() != SymbolicKeyword.COMMA) {
				Token t = tokens.getAhead();
				tokens.destroy();
				throw new ParserException("Expected ',' or ')', but " + t + " found");
			}

			tokens.skipToken();
		}

		tokens.skipToken();
		Statement s = parser.parseStatement(tokens);
		if (s == null) {
			Token t = tokens.getAhead();
			tokens.destroy();
			throw new ParserException("Expected Statement, but " + t + " found");
		}

		tokens.join();
		FunctionSignature functionType = new FunctionSignature(type, argTypes.toArray(Type[]::new));
		return new GuestFunction(symbol.name(), functionType, argNames.toArray(String[]::new), s);
	}
}
