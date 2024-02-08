package xyz.mangostudio.mangoscript.text.lexer.token;

public enum SymbolicKeyword implements Token {
	OPEN_CURLY_BRACKET("{"),
	CLOSE_CURLY_BRACKET("}"),
	SEMICOLON(";"),
	INCREMENT("++"),
	DECREMENT("--"),
	PLUS("+"),
	MINUS("-"),
	POWER("**"),
	MULTIPLY("*"),
	DIVIDE("/"),
	AND("&"),
	OR("|"),
	XOR("^"),
	NOT("!"),
	DOT("."),
	LESS_THAN_OR_EQUALS("<="),
	GREATER_THAN_OR_EQUALS(">="),
	LESS_THAN("<"),
	GREATER_THAN(">"),
	EQUALS("=="),
	ARROW("=>"),
	ASSIGN("="),
	NULL_COALESCING("??"),
	QUESTION_MARK("?"),
	COLON(":"),
	COMMA(","),
	OPEN_PARENTHESES("("),
	CLOSE_PARENTHESES(")"),
	ARRAY("[]"),
	OPEN_SQUARE_BRACKET("["),
	CLOSE_SQUARE_BRACKET("]");

	private String keyword;

	private SymbolicKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() { return keyword; }

	public static final TokensFactory FACTORY = stream -> {
		for (SymbolicKeyword keyword : SymbolicKeyword.values()) if (stream.isNext(keyword.keyword)) {
			stream.advanceBy(keyword.keyword.length());
			return keyword;
		}

		return null;
	};

	@Override
	public String toString() {
		return keyword;
	}
}
