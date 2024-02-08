package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.WhileStatement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class WhileStatementParser implements Parser {
	public static final WhileStatementParser PARSER = new WhileStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() == Keyword.WHILE) {
			tokens.skipToken();

			if (tokens.getAhead() != SymbolicKeyword.OPEN_PARENTHESES)
				throw new ParserException("Expected '(', but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Expression expr = allParsers.parseExpression(tokens);
			if (expr == null) throw new ParserException("Expected Expression, but " + tokens.getAhead() + " found");

			if (tokens.getAhead() != SymbolicKeyword.CLOSE_PARENTHESES)
				throw new ParserException("Expected ')', but " + tokens.getAhead() + " found");
			tokens.skipToken();

			Statement whileTrue = allParsers.parseStatement(tokens);
			if (whileTrue == null) throw new ParserException("Expected Statement, but " + tokens.getAhead() + " found");
			return new WhileStatement(expr, whileTrue);
		}

		return null;
	}
}
