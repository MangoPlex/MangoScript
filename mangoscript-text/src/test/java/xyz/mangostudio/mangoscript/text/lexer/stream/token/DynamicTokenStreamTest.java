package xyz.mangostudio.mangoscript.text.lexer.stream.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;

class DynamicTokenStreamTest {
	@Test
	void testGetAt() {
		Token[] tokens = Keyword.values();
		AtomicInteger position = new AtomicInteger(0);
		DynamicTokenStream stream = new DynamicTokenStream(() -> position.get() < tokens.length
			? tokens[position.getAndAdd(1)]
			: null);
		assertEquals(Keyword.values()[2], stream.getAt(2));
		stream.skipToken();
		assertEquals(Keyword.values()[2], stream.getAt(2));
		stream.skipToken();
		assertEquals(Keyword.values()[2], stream.getAt(2));
		stream.skipTokens(Keyword.values().length - 2);
		assertFalse(stream.hasNext());
	}

	@Test
	void testBranching() {
		Token[] tokens = Keyword.values();
		AtomicInteger position = new AtomicInteger(0);
		DynamicTokenStream stream = new DynamicTokenStream(() -> position.get() < tokens.length
			? tokens[position.getAndAdd(1)]
			: null);
		TokenStream fork = stream.fork();

		assertEquals(Keyword.values()[0], stream.getAhead());
		stream.skipTokens(2);

		assertEquals(Keyword.values()[0], fork.getAhead());
		fork.skipTokens(3);
		fork.join();

		assertEquals(Keyword.values()[3], stream.getAhead());
	}
}
