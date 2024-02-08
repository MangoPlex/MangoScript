package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.ConditionalStatement;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class ConditionalStatementParser implements Parser {
	public static final ConditionalStatementParser PARSER = new ConditionalStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() == Keyword.IF) {
			tokens.skipToken();

			if (tokens.getAhead() != SymbolicKeyword.OPEN_PARENTHESES)
				throw new ParserException("Expected '(', but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Expression expr = allParsers.parseExpression(tokens);
			if (expr == null) throw new ParserException("Expected Expression, but " + tokens.getAhead() + " found");

			if (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES)
				throw new ParserException("Expected ')', but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Statement ifTrue = allParsers.parseStatement(tokens);
			if (ifTrue == null) throw new ParserException("Expected Statement, but " + tokens.getAhead() + " found");

			if (tokens.getAhead() == Keyword.ELSE) {
				tokens.skipToken();

				Statement ifFalse = allParsers.parseStatement(tokens);
				if (ifFalse == null)
					throw new ParserException("Expected Statement, but " + tokens.getAhead() + " found");
				return new ConditionalStatement(expr, ifTrue, ifFalse);
			}

			return new ConditionalStatement(expr, ifTrue);
		}

		return null;
	}
}
