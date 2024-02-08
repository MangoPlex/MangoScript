package xyz.mangostudio.mangoscript.text.parser;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.LabeledLoopingStatement;
import xyz.mangostudio.mangoscript.binary.stmt.looping.LoopingStatement;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Symbol;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;

public class LabeledLoopingStatementParser implements Parser {
	public static final LabeledLoopingStatementParser PARSER = new LabeledLoopingStatementParser();

	@Override
	public Statement parseStatement(Parser allParsers, TokenStream tokens) {
		if (tokens.getAhead() instanceof Symbol label) {
			TokenStream fork = tokens.fork();
			fork.skipToken();

			if (fork.getAhead() != SymbolicKeyword.COLON) {
				fork.destroy();
				return null;
			}

			fork.skipToken();
			fork.join();

			Statement statement = allParsers.parseStatement(tokens);
			if (statement == null)
				throw new ParserException("Expected LoopingStatement, but " + tokens.getAhead() + " found");
			if (!(statement instanceof LoopingStatement ls))
				throw new ParserException("Expected LoopingStatement, but " + statement + " found");
			return new LabeledLoopingStatement(label.name(), ls);
		}

		return null;
	}
}
