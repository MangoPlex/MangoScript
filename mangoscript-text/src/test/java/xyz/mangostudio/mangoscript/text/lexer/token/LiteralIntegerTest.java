package xyz.mangostudio.mangoscript.text.lexer.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;

class LiteralIntegerTest {
	@Test
	void testDec() {
		ReaderStringStream stream = new ReaderStringStream("12S12L123.2");
		assertEquals(new LiteralInteger("12", 's'), LiteralInteger.DEC_FACTORY.nextToken(stream));
		assertEquals(new LiteralInteger("12", 'l'), LiteralInteger.DEC_FACTORY.nextToken(stream));
		assertNull(LiteralInteger.DEC_FACTORY.nextToken(stream));
	}

	@Test
	void testOct() {
		ReaderStringStream stream = new ReaderStringStream("012I032S");
		assertEquals(new LiteralInteger("012", 'i'), LiteralInteger.OCT_FACTORY.nextToken(stream));
		assertEquals(new LiteralInteger("032", 's'), LiteralInteger.OCT_FACTORY.nextToken(stream));
	}

	@Test
	void testHex() {
		ReaderStringStream stream = new ReaderStringStream("0x12i0xdeadbeefs");
		assertEquals(new LiteralInteger("0x12", 'i'), LiteralInteger.HEX_FACTORY.nextToken(stream));
		assertEquals(new LiteralInteger("0xdeadbeef", 's'), LiteralInteger.HEX_FACTORY.nextToken(stream));
	}

	@Test
	void testCombined() {
		StringStreamLexer lexer = new StringStreamLexer(TokensFactory.combine(
			VoidToken.FACTORY,
			Keyword.FACTORY,
			SymbolicKeyword.FACTORY,
			LiteralInteger.FACTORY), new ReaderStringStream("while (1 > 0x05s) {}"));
		assertEquals(Keyword.WHILE, lexer.nextToken());
		assertEquals(SymbolicKeyword.OPEN_PARENTHESES, lexer.nextToken());
		assertEquals(new LiteralInteger("1", 'i'), lexer.nextToken());
		assertEquals(SymbolicKeyword.GREATER_THAN, lexer.nextToken());
		assertEquals(new LiteralInteger("0x05", 's'), lexer.nextToken());
		assertEquals(SymbolicKeyword.CLOSE_PARENTHESES, lexer.nextToken());
		assertEquals(SymbolicKeyword.OPEN_CURLY_BRACKET, lexer.nextToken());
		assertEquals(SymbolicKeyword.CLOSE_CURLY_BRACKET, lexer.nextToken());
	}
}
