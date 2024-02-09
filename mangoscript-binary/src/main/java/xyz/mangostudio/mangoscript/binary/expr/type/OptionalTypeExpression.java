package xyz.mangostudio.mangoscript.binary.expr.type;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.expr.PropertyGetExpression;

public record OptionalTypeExpression(Expression parent) implements TypeExpression {
	public OptionalTypeExpression {
		if ((parent instanceof PropertyGetExpression getter && !getter.canBeUsedAsType()) &&
			!(parent instanceof TypeExpression))
			throw new RuntimeException("Can't use " + parent + " as type to make optional variant");
	}

	@Override
	public String toString() {
		if (parent instanceof OptionalTypeExpression) return "(" + parent + ")?";
		return parent + "?";
	}
}
