package xyz.mangostudio.mangoscript.text.lexer.token;

import xyz.mangostudio.mangoscript.binary.expr.literal.LiteralNumberExpression;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.StringStream;

public record LiteralFloat(String content, char suffix) implements Token {
	private static final String VALID_DEC = "0123456789";

	public LiteralNumberExpression toExpression() {
		return switch (suffix) {
		case 'f' -> new LiteralNumberExpression(PrimitiveType.F32, Float.floatToIntBits(Float.parseFloat(content)));
		case 'd' -> new LiteralNumberExpression(PrimitiveType.F64, Double.doubleToLongBits(Float.parseFloat(content)));
		default -> throw new RuntimeException("Invalid suffix: " + suffix);
		};
	}

	public static final TokensFactory FACTORY = stream -> {
		if (!stream.isAvailable()) return null;
		StringStream fork = stream.fork();
		String c;

		if (VALID_DEC.indexOf(c = fork.getAhead(1)) != -1) {
			fork.advanceBy(1);
			String content = c;

			while (fork.isAvailable() && VALID_DEC.indexOf(c = fork.getAhead(1)) != -1) {
				fork.advanceBy(1);
				content += c;
			}

			if (!fork.isAvailable()) {
				// Could be an integer
				fork.destroy();
				return null;
			}

			if ("fFdD".indexOf(c = fork.getAhead(1)) != -1) {
				fork.advanceBy(1);
				fork.join();
				return new LiteralFloat(content + ".0", c.toLowerCase().charAt(0));
			}

			if (!(c = fork.getAhead(1)).equals(".")) {
				fork.destroy();
				return null;
			}

			fork.advanceBy(1);
			content += ".";

			while (fork.isAvailable() && VALID_DEC.indexOf(c = fork.getAhead(1)) != -1) {
				fork.advanceBy(1);
				content += c;
			}

			if (fork.isAvailable() && "fFdD".indexOf(c = fork.getAhead(1)) != -1) {
				fork.advanceBy(1);
				fork.join();
				return new LiteralFloat(content, c.toLowerCase().charAt(0));
			}

			if (content.endsWith(".")) content += "0";
			fork.join();
			return new LiteralFloat(content, 'f');
		} else {
			fork.destroy();
			return null;
		}
	};
}
