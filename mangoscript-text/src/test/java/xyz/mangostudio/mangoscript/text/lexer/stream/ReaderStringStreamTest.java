package xyz.mangostudio.mangoscript.text.lexer.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Reader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.StringStream;

class ReaderStringStreamTest {
	@Test
	void testNoForking() {
		Reader reader = new StringReader("according to all laws of aviation, there is no way a bee should be able to fly");
		ReaderStringStream stream = new ReaderStringStream(reader);
		assertEquals("accordin", stream.getAhead(8));
		stream.advanceBy(8);
		assertEquals("g to all ", stream.getAhead(9));
		stream.advanceBy(9);
		assertTrue(stream.isAvailable());
	}

	@Test
	void testForking() {
		Reader reader = new StringReader("according to all laws of aviation, there is no way a bee should be able to fly");
		ReaderStringStream stream = new ReaderStringStream(reader);
		StringStream forked = stream.fork();

		assertEquals("accordin", stream.getAhead(8));
		stream.advanceBy(8);

		assertEquals("according", forked.getAhead(9));
		forked.advanceBy(9);
		forked.join();

		assertEquals(" to", stream.getAhead(3));
		stream.advanceBy(3);
	}
}
