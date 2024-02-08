package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.DefineVariableStatement;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Symbol;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;

public class DefineStatementParser implements Parser {
	public static final DefineStatementParser PARSER = new DefineStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		TokenStream fork = tokens.fork();

		Type type = TypeParser.parseType(fork);
		if (type == null) {
			fork.destroy();
			return null;
		}

		if (!(fork.getAhead() instanceof Symbol symbol)) {
			fork.destroy();
			return null;
		}

		fork.skipToken();

		if (fork.getAhead() == SymbolicKeyword.ASSIGN) {
			fork.skipToken();
			Expression expr = allParsers.parseExpression(fork);

			if (expr == null) {
				Token t = fork.getAhead();
				fork.destroy();
				throw new ParserException("Expected Expression, but " + t + " found");
			}

			if (fork.getAhead() != SymbolicKeyword.SEMICOLON) {
				Token t = fork.getAhead();
				fork.destroy();
				throw new ParserException("Expected ';', but " + t + " found");
			}

			fork.skipToken();
			fork.join();
			return new DefineVariableStatement(type, symbol.name(), expr);
		} else if (fork.getAhead() == SymbolicKeyword.SEMICOLON) {
			fork.skipToken();
			fork.join();
			return new DefineVariableStatement(type, symbol.name());
		} else {
			Token t = fork.getAhead();
			fork.destroy();
			throw new ParserException("Expected '=' or ';', but " + t + " found");
		}
	}
}
