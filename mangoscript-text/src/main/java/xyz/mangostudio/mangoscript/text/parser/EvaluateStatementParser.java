package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.EvaluateStatement;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class EvaluateStatementParser implements Parser {
	public static final EvaluateStatementParser PARSER = new EvaluateStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		Expression expr = allParsers.parseExpression(tokens);
		if (expr == null) return null;
		if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
			throw new ParserException("Expected ';' but " + tokens.getAhead() + " found");
		tokens.skipToken();
		return new EvaluateStatement(expr);
	}
}
