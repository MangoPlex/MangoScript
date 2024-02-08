package xyz.mangostudio.mangoscript.text.lexer.stream.token;

import xyz.mangostudio.mangoscript.text.lexer.stream.LexerStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;

public interface TokenStream extends LexerStream<TokenStream> {
	public boolean hasNext();

	public Token getAhead();

	public boolean skipToken();

	default int skipTokens(int amount) {
		int skipped = 0;

		while (skipped < amount) {
			if (!skipToken()) break;
			skipped++;
		}

		return skipped;
	}

	public int getPosition();
}
