package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.ForStatement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class ForStatementParser implements Parser {
	public static final ForStatementParser PARSER = new ForStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() == Keyword.FOR) {
			tokens.skipToken();

			if (tokens.getAhead() != SymbolicKeyword.OPEN_PARENTHESES)
				throw new ParserException("Expected '(' but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Statement initial = allParsers.parseStatement(tokens);
			if (initial == null && tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected Statement or ';' but " + tokens.getAhead() + " found");
			if (tokens.getAhead() == SymbolicKeyword.SEMICOLON) tokens.skipToken();

			Expression condition = allParsers.parseExpression(tokens);
			if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected ';' but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Expression next = allParsers.parseExpression(tokens);
			if (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES)
				throw new ParserException("Expected ')' but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Statement whileTrue = allParsers.parseStatement(tokens);
			if (whileTrue == null) throw new ParserException("Expected Statement but " + tokens.getAhead() + " found");
			return new ForStatement(initial, condition, next, whileTrue);
		}

		return null;
	}
}
