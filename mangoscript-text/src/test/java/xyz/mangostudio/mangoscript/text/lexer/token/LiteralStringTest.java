package xyz.mangostudio.mangoscript.text.lexer.token;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;

class LiteralStringTest {
	@Test
	void test() {
		StringStreamLexer lexer = new StringStreamLexer(LiteralString.FACTORY, new ReaderStringStream("'nahkd\\'s money\\u003b'"));
		assertEquals(new LiteralString("nahkd's money\u003b"), lexer.nextToken());
	}
}
