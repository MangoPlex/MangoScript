package xyz.mangostudio.mangoscript.text.lexer.token;

public interface Token {
	public static final TokensFactory FACTORY = TokensFactory.combine(
		VoidToken.FACTORY,
		Keyword.FACTORY,
		SymbolicKeyword.FACTORY,
		Symbol.FACTORY,
		LiteralFloat.FACTORY,
		LiteralInteger.FACTORY,
		LiteralString.FACTORY);
}
