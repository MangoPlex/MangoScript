package xyz.mangostudio.mangoscript.text.lexer.token;

import xyz.mangostudio.mangoscript.text.lexer.Lexer;
import xyz.mangostudio.mangoscript.text.lexer.LexerException;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.StringStream;

/**
 * <p>
 * Void tokens are mainly ignored by {@link Lexer} when reading tokens from i.e:
 * {@link StringStream} or an array of tokens.
 * </p>
 * <p>
 * Mainly used in place of whitespaces and comments.
 * </p>
 */
public class VoidToken implements Token {
	public static final VoidToken VOID = new VoidToken();

	private VoidToken() {}

	private static final char[] WHITESPACES = { ' ', '\t', '\r', '\n' };

	public static final TokensFactory WHITESPACE_FACTORY = stream -> {
		if (!stream.isAvailable()) return null;
		char c = stream.getAhead(1).charAt(0);

		for (char ws : WHITESPACES) if (ws == c) {
			stream.advanceBy(1);
			return VOID;
		}

		return null;
	};

	public static final TokensFactory SL_COMMENT_FACTORY = stream -> {
		if (stream.isNext("//")) {
			stream.advanceBy(2);
			while (stream.isAvailable() && !stream.isNext("\n")) stream.advanceBy(1);
			stream.advanceBy(1);
			return VOID;
		}

		return null;
	};

	public static final TokensFactory ML_COMMENT_FACTORY = stream -> {
		if (stream.isNext("/*")) {
			stream.advanceBy(2);

			while (!stream.isNext("*/")) {
				if (!stream.isAvailable()) throw new LexerException("Expected '*/', but end of stream found.");
				stream.advanceBy(1);
			}

			stream.advanceBy(2);
			return VOID;
		}

		return null;
	};

	public static final TokensFactory COMMENT_FACTORY = TokensFactory.combine(
		SL_COMMENT_FACTORY,
		ML_COMMENT_FACTORY);
	public static final TokensFactory FACTORY = TokensFactory.combine(
		WHITESPACE_FACTORY,
		SL_COMMENT_FACTORY,
		ML_COMMENT_FACTORY);
}
