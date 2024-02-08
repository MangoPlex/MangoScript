package xyz.mangostudio.mangoscript.binary.expr;

public record PropertySetExpression(Expression target, String name, Expression expression) implements Expression {
	@Override
	public String toString() {
		if (target == LocalExpression.LOCAL) return name + " = " + expression;
		return target + "." + name + " = " + expression;
	}
}
