package xyz.mangostudio.mangoscript.text.lexer.token;

import xyz.mangostudio.mangoscript.binary.expr.literal.LiteralNumberExpression;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.text.lexer.LexerException;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.StringStream;

public record LiteralInteger(String content, char suffix) implements Token {
	public boolean isHex() { return content.startsWith("0x"); }

	public boolean isOct() { return content.startsWith("0") && content.length() >= 2; }

	public boolean isBin() { return content.startsWith("0b"); }

	public boolean isDec() { return !isHex() && !isOct() && !isBin(); }

	@Override
	public String toString() {
		return content + suffix;
	}

	public LiteralNumberExpression toExpression() {
		PrimitiveType type = switch (suffix) {
		case 'b', 'y' -> PrimitiveType.I8;
		case 's' -> PrimitiveType.I16;
		case 'i' -> PrimitiveType.I32;
		case 'l' -> PrimitiveType.I64;
		default -> throw new RuntimeException("Invalid suffix: " + suffix);
		};

		int base = isHex() ? 16 : isBin() ? 2 : isOct() ? 8 : 10;
		String v = (isHex() || isBin()) ? content.substring(2) : isOct() ? content.substring(1) : content;
		return new LiteralNumberExpression(type, Long.parseLong(v, base));
	}

	private static final String SUFFIXES = "bysil"; // byte, byte (alt), short, int, long
	private static final String VALID_DEC_PREFIX = "123456789";
	private static final String VALID_DEC = VALID_DEC_PREFIX + "0";
	private static final String VALID_OCT = "01234567";
	private static final String VALID_HEX = "0123456789abcdefABCDEF";

	public static final TokensFactory DEC_FACTORY = stream -> {
		if (!stream.isAvailable()) return null;
		StringStream fork = stream.fork();

		if (VALID_DEC_PREFIX.indexOf(fork.getAhead(1)) != -1) {
			String content = fork.getAhead(1);
			String c;
			fork.advanceBy(1);

			while (fork.isAvailable() && VALID_DEC.indexOf(c = fork.getAhead(1)) != -1) {
				content += c;
				fork.advanceBy(1);
			}

			if (!fork.isAvailable()) {
				fork.join();
				return new LiteralInteger(content, 'i');
			}

			if (SUFFIXES.indexOf(c = fork.getAhead(1).toLowerCase()) != -1) {
				fork.advanceBy(1);
				fork.join();
				return new LiteralInteger(content, c.charAt(0));
			}

			if ("fdFD.".indexOf(c = fork.getAhead(1)) != -1) {
				// Could be a float or double
				fork.destroy();
				return null;
			}

			fork.join();
			return new LiteralInteger(content, 'i');
		}

		return null;
	};

	public static final TokensFactory OCT_FACTORY = stream -> {
		if (!stream.isAvailable()) return null;

		if (stream.isNext("0") && !stream.isNext("0x")) {
			stream.advanceBy(1);
			String content = "0";
			String c;

			while (stream.isAvailable() && VALID_OCT.indexOf(c = stream.getAhead(1)) != -1) {
				content += c;
				stream.advanceBy(1);
			}

			if (!stream.isAvailable()) return new LiteralInteger(content, 'i');
			else if (VALID_DEC.indexOf(c = stream.getAhead(1)) != -1) {
				throw new LexerException("Expected [0-7], but '" + c + "' found.");
			}

			if (SUFFIXES.indexOf(c = stream.getAhead(1).toLowerCase()) != -1) {
				stream.advanceBy(1);
				stream.join();
				return new LiteralInteger(content, c.charAt(0));
			}

			return new LiteralInteger(content, 'i');
		}

		return null;
	};

	public static final TokensFactory HEX_FACTORY = stream -> {
		if (!stream.isAvailable()) return null;

		if (stream.isNext("0x")) {
			stream.advanceBy(2);
			String content = "0x";
			String c;

			while (stream.isAvailable() && VALID_HEX.indexOf(c = stream.getAhead(1)) != -1) {
				content += c;
				stream.advanceBy(1);
			}

			if (!stream.isAvailable()) return new LiteralInteger(content, 'i');

			if (SUFFIXES.indexOf(c = stream.getAhead(1).toLowerCase()) != -1) {
				stream.advanceBy(1);
				stream.join();
				return new LiteralInteger(content, c.charAt(0));
			}

			return new LiteralInteger(content, 'i');
		}

		return null;
	};

	public static final TokensFactory BIN_FACTORY = stream -> {
		if (!stream.isAvailable()) return null;

		if (stream.isNext("0b")) {
			stream.advanceBy(2);
			String content = "0b";
			String c;

			while (stream.isAvailable() && "01".indexOf(c = stream.getAhead(1)) != -1) {
				content += c;
				stream.advanceBy(1);
			}

			if (!stream.isAvailable()) return new LiteralInteger(content, 'i');

			if (SUFFIXES.indexOf(c = stream.getAhead(1).toLowerCase()) != -1) {
				stream.advanceBy(1);
				stream.join();
				return new LiteralInteger(content, c.charAt(0));
			}

			return new LiteralInteger(content, 'i');
		}

		return null;
	};

	public static final TokensFactory FACTORY = TokensFactory.combine(
		HEX_FACTORY,
		BIN_FACTORY,
		OCT_FACTORY,
		DEC_FACTORY);
}
