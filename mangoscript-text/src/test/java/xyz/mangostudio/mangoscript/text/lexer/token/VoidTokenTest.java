package xyz.mangostudio.mangoscript.text.lexer.token;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;

class VoidTokenTest {
	@Test
	void testWhitespaces() {
		TokensFactory factory = TokensFactory.combine(
			VoidToken.WHITESPACE_FACTORY,
			Keyword.FACTORY,
			SymbolicKeyword.FACTORY);
		StringStreamLexer lexer = new StringStreamLexer(factory, new ReaderStringStream("while\n(true)"));
		List<Token> tokens = lexer.toTokensList();
		assertEquals(Keyword.WHILE, tokens.get(0));
		assertEquals(SymbolicKeyword.OPEN_PARENTHESES, tokens.get(1));
		assertEquals(Keyword.TRUE, tokens.get(2));
		assertEquals(SymbolicKeyword.CLOSE_PARENTHESES, tokens.get(3));
	}

	@Test
	void testSingleLineComments() {
		TokensFactory factory = TokensFactory.combine(
			VoidToken.WHITESPACE_FACTORY,
			VoidToken.SL_COMMENT_FACTORY,
			Keyword.FACTORY,
			SymbolicKeyword.FACTORY);
		StringStreamLexer lexer = new StringStreamLexer(factory, new ReaderStringStream("// my comment\nwhile //my comment\n(true)"));
		List<Token> tokens = lexer.toTokensList();
		assertEquals(Keyword.WHILE, tokens.get(0));
		assertEquals(SymbolicKeyword.OPEN_PARENTHESES, tokens.get(1));
		assertEquals(Keyword.TRUE, tokens.get(2));
		assertEquals(SymbolicKeyword.CLOSE_PARENTHESES, tokens.get(3));
	}

	@Test
	void testMultiLineComments() {
		TokensFactory factory = TokensFactory.combine(
			VoidToken.WHITESPACE_FACTORY,
			VoidToken.ML_COMMENT_FACTORY,
			Keyword.FACTORY,
			SymbolicKeyword.FACTORY);
		StringStreamLexer lexer = new StringStreamLexer(factory, new ReaderStringStream("while /**/ (true) /* comment */"));
		List<Token> tokens = lexer.toTokensList();
		assertEquals(Keyword.WHILE, tokens.get(0));
		assertEquals(SymbolicKeyword.OPEN_PARENTHESES, tokens.get(1));
		assertEquals(Keyword.TRUE, tokens.get(2));
		assertEquals(SymbolicKeyword.CLOSE_PARENTHESES, tokens.get(3));
	}
}
