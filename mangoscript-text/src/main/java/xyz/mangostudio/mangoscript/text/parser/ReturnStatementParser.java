package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.ReturnStatement;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class ReturnStatementParser implements Parser {
	public static final ReturnStatementParser PARSER = new ReturnStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() == Keyword.RETURN) {
			tokens.skipToken();
			Expression expr = allParsers.parseExpression(tokens);
			if (expr == null) throw new ParserException("Expected expression, but " + tokens.getAhead() + " found");

			if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected ';', but " + tokens.getAhead() + " found");
			tokens.skipToken();
			return new ReturnStatement(expr);
		}

		return null;
	}
}
