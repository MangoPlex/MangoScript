package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.BreakStatement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.Symbol;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class BreakStatementParser implements Parser {
	public static final BreakStatementParser PARSER = new BreakStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() == Keyword.BREAK) {
			tokens.skipToken();

			String labelName = null;
			if (tokens.getAhead() instanceof Symbol label) {
				tokens.skipToken();
				labelName = label.name();
			}

			if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected ';', but " + tokens.getAhead() + " found");

			tokens.skipToken();
			return new BreakStatement(labelName);
		}

		return null;
	}
}
