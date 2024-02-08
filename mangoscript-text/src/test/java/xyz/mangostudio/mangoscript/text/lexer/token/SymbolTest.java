package xyz.mangostudio.mangoscript.text.lexer.token;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;

class SymbolTest {
	@Test
	void test() {
		TokensFactory factory = TokensFactory.combine(VoidToken.WHITESPACE_FACTORY, Keyword.FACTORY,
			SymbolicKeyword.FACTORY, Symbol.FACTORY);
		StringStreamLexer lexer = new StringStreamLexer(factory, new ReaderStringStream("while (myVar == true)"));
		List<Token> tokens = lexer.toTokensList();
		assertEquals(Keyword.WHILE, tokens.get(0));
		assertEquals(SymbolicKeyword.OPEN_PARENTHESES, tokens.get(1));
		assertEquals(new Symbol("myVar"), tokens.get(2));
		assertEquals(SymbolicKeyword.EQUALS, tokens.get(3));
		assertEquals(Keyword.TRUE, tokens.get(4));
		assertEquals(SymbolicKeyword.CLOSE_PARENTHESES, tokens.get(5));
	}
}
