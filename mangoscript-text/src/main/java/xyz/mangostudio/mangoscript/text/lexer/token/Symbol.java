package xyz.mangostudio.mangoscript.text.lexer.token;

public record Symbol(String name) implements Token {
	private static final String ALLOWED_PREFIX = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$";
	private static final String ALLOWED_NEXT = ALLOWED_PREFIX + "0123456789";

	public static final TokensFactory FACTORY = stream -> {
		if (!stream.isAvailable()) return null;
		String t;
		int idx = ALLOWED_PREFIX.indexOf(t = stream.getAhead(1));

		if (idx != -1) {
			String c;
			stream.advanceBy(1);

			while (stream.isAvailable() && (idx = ALLOWED_NEXT.indexOf(c = stream.getAhead(1))) != -1) {
				stream.advanceBy(1);
				t += c;
			}

			return new Symbol(t);
		}

		return null;
	};

	@Override
	public String toString() {
		return "Symbol(" + name + ")";
	}
}
