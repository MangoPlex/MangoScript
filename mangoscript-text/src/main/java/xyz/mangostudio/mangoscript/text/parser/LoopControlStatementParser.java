package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.LoopControlStatement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class LoopControlStatementParser implements Parser {
	public static final LoopControlStatementParser PARSER = new LoopControlStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() == Keyword.BREAK) {
			tokens.skipToken();

			if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected ';', but " + tokens.getAhead() + " found");
			tokens.skipToken();
			return LoopControlStatement.BREAK;
		}

		if (tokens.getAhead() == Keyword.CONTINUE) {
			tokens.skipToken();

			if (tokens.getAhead() != SymbolicKeyword.SEMICOLON)
				throw new ParserException("Expected ';', but " + tokens.getAhead() + " found");
			tokens.skipToken();
			return LoopControlStatement.CONTINUE;
		}

		return null;
	}
}
