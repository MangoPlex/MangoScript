package xyz.mangostudio.mangoscript.text.lexer.token;

import java.util.Map;

import xyz.mangostudio.mangoscript.text.lexer.LexerException;

public record LiteralString(String content) implements Token {

	@Override
	public String toString() {
		return "\"" + content + "\"";
	}

	private static final Map<String, String> ESCAPE_MAP = Map.of(
		"r", "\r",
		"n", "\n",
		"t", "\t");

	private static final String VALID_HEX = "0123456789abcdefABCDEF";

	public static final TokensFactory FACTORY = stream -> {
		if (!stream.isAvailable()) return null;

		String head = null;
		if (stream.isNext("\"")) head = "\"";
		if (stream.isNext("'")) head = "'";
		if (head == null) return null;

		stream.advanceBy(1);
		boolean escaping = false;
		String unicode = null; // uXXXX
		String content = "";

		while (true) {
			if (!stream.isAvailable())
				throw new LexerException("Expected (" + head + "), '\\' or a character, but end of stream found.");
			String c = stream.getAhead(1);
			stream.advanceBy(1);

			if (!escaping) {
				if (c.equals("\\")) {
					escaping = true;
					continue;
				}

				if (c.equals(head)) { return new LiteralString(content); }

				content += c;
			} else if (unicode == null) {
				String escaped = ESCAPE_MAP.get(c);

				if (escaped != null) {
					content += escaped;
					escaping = false;
					continue;
				}

				if (c.equals(head)) {
					content += head;
					escaping = false;
					continue;
				}

				if (c.equals("u")) {
					unicode = "u";
					continue;
				}

				throw new LexerException("Expected 'r', 'n', 't', 'u????' or (" + head + "), but '" + c + "' found.");
			} else if (unicode.startsWith("u")) {
				if (VALID_HEX.indexOf(c) == -1)
					throw new LexerException("Expected [0-9A-Fa-f], but '" + c + "' found.");
				unicode += c;

				if (unicode.length() == 5) {
					int code = Integer.parseInt(unicode.substring(1), 16);
					content += (char) code;
					escaping = false;
					unicode = null;
					continue;
				}
			}
		}
	};
}
