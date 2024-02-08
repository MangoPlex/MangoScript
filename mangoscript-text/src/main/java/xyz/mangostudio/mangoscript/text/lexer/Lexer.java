package xyz.mangostudio.mangoscript.text.lexer;

import java.util.ArrayList;
import java.util.List;

import xyz.mangostudio.mangoscript.text.lexer.token.Token;

public interface Lexer {
	public Token nextToken();

	default List<Token> toTokensList() {
		List<Token> tokens = new ArrayList<>();
		Token token;
		while ((token = nextToken()) != null) tokens.add(token);
		return tokens;
	}
}
