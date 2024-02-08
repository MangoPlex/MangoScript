package xyz.mangostudio.mangoscript.text.parser;

import java.util.ArrayList;
import java.util.List;

import xyz.mangostudio.mangoscript.binary.stmt.ScopeStatement;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class ScopeStatementParser implements Parser {
	public static final ScopeStatementParser PARSER = new ScopeStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() == SymbolicKeyword.OPEN_CURLY_BRACKET) {
			tokens.skipToken();

			List<Statement> statements = new ArrayList<>();
			while (tokens.getAhead() != SymbolicKeyword.CLOSE_CURLY_BRACKET) {
				Statement s = allParsers.parseStatement(tokens);
				if (s == null)
					throw new ParserException("Expected Statement or '}', but " + tokens.getAhead() + " found");
				statements.add(s);
			}

			tokens.skipToken();
			return new ScopeStatement(statements.toArray(Statement[]::new));
		}

		return null;
	}
}
