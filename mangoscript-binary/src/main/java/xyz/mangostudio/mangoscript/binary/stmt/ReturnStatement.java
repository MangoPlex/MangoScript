package xyz.mangostudio.mangoscript.binary.stmt;

import xyz.mangostudio.mangoscript.binary.expr.Expression;

public record ReturnStatement(Expression expression) implements Statement {
	@Override
	public String toString() {
		return "return " + expression;
	}
}
