package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;

public interface Parser {
	default Statement parseStatement(Parser allParsers, TokenStream tokens) {
		return null;
	}

	/**
	 * <p>
	 * Parse statements, using the current parser object as "all parsers". If you
	 * are using a branch of parser, you might want to pass "all parsers" to
	 * {@link #parseStatement(Parser, TokenStream)} instead.
	 * </p>
	 * 
	 * @param tokens A forkable stream of tokens.
	 * @return Parsed statement, or {@code null} if the statement can't be parsed by
	 *         this parser.
	 */
	default Statement parseStatement(TokenStream tokens) {
		return parseStatement(this, tokens);
	}

	default Expression parseExpression(Parser allParsers, TokenStream tokens) {
		return null;
	}

	default Expression parseExpression(TokenStream tokens) {
		return parseExpression(this, tokens);
	}

	public static Parser combine(Parser... parsers) {
		return new Parser() {
			@Override
			public Statement parseStatement(Parser allParsers, TokenStream tokens) {
				for (Parser parser : parsers) {
					Statement s = parser.parseStatement(allParsers, tokens);
					if (s != null) return s;
				}

				return null;
			}

			@Override
			public Expression parseExpression(Parser allParsers, TokenStream tokens) {
				for (Parser parser : parsers) {
					Expression e = parser.parseExpression(allParsers, tokens);
					if (e != null) return e;
				}

				return null;
			}
		};
	}
}
