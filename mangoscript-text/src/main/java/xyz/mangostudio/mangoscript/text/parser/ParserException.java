package xyz.mangostudio.mangoscript.text.parser;

import java.io.Serial;

public class ParserException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 6381147889001243457L;

	public ParserException(String message) {
		super(message);
	}
}
