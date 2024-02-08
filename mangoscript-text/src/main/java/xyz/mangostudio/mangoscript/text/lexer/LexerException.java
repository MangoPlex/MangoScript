package xyz.mangostudio.mangoscript.text.lexer;

import java.io.Serial;

public class LexerException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -7722619949786299951L;

	public LexerException(String message) {
		super(message);
	}
}
