package xyz.mangostudio.mangoscript.text.lexer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static xyz.mangostudio.mangoscript.text.lexer.token.Keyword.FALSE;
import static xyz.mangostudio.mangoscript.text.lexer.token.Keyword.IF;
import static xyz.mangostudio.mangoscript.text.lexer.token.Keyword.TRUE;
import static xyz.mangostudio.mangoscript.text.lexer.token.Keyword.WHILE;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.ARRAY;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.ARROW;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.CLOSE_CURLY_BRACKET;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.CLOSE_PARENTHESES;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.GREATER_THAN_OR_EQUALS;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.OPEN_CURLY_BRACKET;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.OPEN_PARENTHESES;
import static xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword.PLUS;

import java.io.StringReader;
import java.util.List;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Keyword;
import xyz.mangostudio.mangoscript.text.lexer.token.SymbolicKeyword;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;
import xyz.mangostudio.mangoscript.text.lexer.token.TokensFactory;

class StringStreamLexerTest {
	@Test
	void testKeywords() {
		ReaderStringStream stream = new ReaderStringStream(new StringReader("while(true){if((true+false)>=true){true=>false[]}}"));
		StringStreamLexer lexer = new StringStreamLexer(TokensFactory.combine(
			Keyword.FACTORY,
			SymbolicKeyword.FACTORY), stream);
		List<Token> tokens = lexer.toTokensList();
		Token[] keywords = new Token[] {
			WHILE, OPEN_PARENTHESES, TRUE, CLOSE_PARENTHESES,
			OPEN_CURLY_BRACKET,
			IF,
			OPEN_PARENTHESES, OPEN_PARENTHESES, TRUE, PLUS, FALSE, CLOSE_PARENTHESES, GREATER_THAN_OR_EQUALS, TRUE,
			CLOSE_PARENTHESES,
			OPEN_CURLY_BRACKET,
			TRUE, ARROW, FALSE, ARRAY,
			CLOSE_CURLY_BRACKET,
			CLOSE_CURLY_BRACKET
		};

		for (int i = 0; i < keywords.length; i++) assertEquals(keywords[i], tokens.get(i));
		System.out.println(tokens);
	}
}
