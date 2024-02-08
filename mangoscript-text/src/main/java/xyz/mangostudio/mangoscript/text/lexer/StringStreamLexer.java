package xyz.mangostudio.mangoscript.text.lexer;

import xyz.mangostudio.mangoscript.text.lexer.stream.string.StringStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;
import xyz.mangostudio.mangoscript.text.lexer.token.TokensFactory;
import xyz.mangostudio.mangoscript.text.lexer.token.VoidToken;

public class StringStreamLexer implements Lexer {
	private TokensFactory factory;
	private StringStream stream;

	public StringStreamLexer(TokensFactory factory, StringStream stream) {
		this.factory = factory;
		this.stream = stream;
	}

	@Override
	public Token nextToken() {
		Token next;
		while ((next = factory.nextToken(stream)) == VoidToken.VOID);
		if (next == null && stream.isAvailable())
			throw new LexerException("Expected a valid token, but '" + stream.getAhead(12) + "...' found");
		return next;
	}

	public TokensFactory getFactory() { return factory; }

	public StringStream getStream() { return stream; }
}
