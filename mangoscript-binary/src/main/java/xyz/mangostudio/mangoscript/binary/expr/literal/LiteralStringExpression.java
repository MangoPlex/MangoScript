package xyz.mangostudio.mangoscript.binary.expr.literal;

import xyz.mangostudio.mangoscript.binary.expr.Expression;

public record LiteralStringExpression(String content) implements Expression {
	@Override
	public String toString() {
		return '"' + content + '"';
	}
}
