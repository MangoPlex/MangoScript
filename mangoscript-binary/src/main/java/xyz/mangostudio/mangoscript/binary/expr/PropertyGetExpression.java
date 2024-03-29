package xyz.mangostudio.mangoscript.binary.expr;

public record PropertyGetExpression(Expression target, String name) implements Expression {
	public PropertySetExpression setTo(Expression expression) {
		return new PropertySetExpression(target, name, expression);
	}

	public boolean canBeUsedAsType() {
		return target == LocalExpression.LOCAL;
	}

	@Override
	public String toString() {
		if (target == LocalExpression.LOCAL) return name;
		return target + "." + name;
	}
}
