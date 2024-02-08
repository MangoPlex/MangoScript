package xyz.mangostudio.mangoscript.text.lexer.token;

import xyz.mangostudio.mangoscript.text.lexer.stream.string.StringStream;

@FunctionalInterface
public interface TokensFactory {
	public Token nextToken(StringStream stream);

	public static TokensFactory combine(TokensFactory... factories) {
		return stream -> {
			for (TokensFactory factory : factories) {
				Token token = factory.nextToken(stream);
				if (token != null) return token;
			}

			return null;
		};
	}
}
