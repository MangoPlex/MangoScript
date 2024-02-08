package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.binary.type.oop.ClassType;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.Symbol;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class TypeParser {
	/**
	 * <p>
	 * Parse type from tokens stream.
	 * </p>
	 * 
	 * @param tokens The stream of tokens.
	 * @return A valid type, or {@code null} if the next token is not a valid type.
	 */
	public static Type parseType(TokenStream tokens) {
		Type type = parseBaseType(tokens);
		if (type == null) return null;

		while (true) {
			if (tokens.getAhead() == SymbolicKeyword.ARRAY) {
				tokens.skipToken();
				type = type.arrayVariant();
				continue;
			}

			if (tokens.getAhead() == SymbolicKeyword.QUESTION_MARK) {
				tokens.skipToken();
				type = type.optionalVariant();
				return type;
			}

			return type;
		}
	}

	/**
	 * <p>
	 * Parse base type from tokens stream. Base types are types that aren't array
	 * ({@code Type[]}) or optional ({@code Type?}).
	 * </p>
	 * 
	 * @param tokens Stream of tokens.
	 * @return A valid base type, or {@code null} if the next token is not a valid
	 *         type.
	 */
	public static Type parseBaseType(TokenStream tokens) {
		if (tokens.getAhead() instanceof Keyword keyword) {
			tokens.skipToken();
			return switch (keyword) {
			case I8 -> PrimitiveType.I8;
			case I16 -> PrimitiveType.I16;
			case I32 -> PrimitiveType.I32;
			case I64 -> PrimitiveType.I64;
			case F32 -> PrimitiveType.F32;
			case F64 -> PrimitiveType.F64;
			case ANY -> PrimitiveType.ANY;
			case VOID -> PrimitiveType.VOID;
			default -> null;
			};
		}

		if (tokens.getAhead() instanceof Symbol typeName) {
			tokens.skipToken();
			return new ClassType(typeName.name());
		}

		return null;
	}
}
