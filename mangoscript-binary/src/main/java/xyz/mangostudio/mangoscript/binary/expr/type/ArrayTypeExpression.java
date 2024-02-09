package xyz.mangostudio.mangoscript.binary.expr.type;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.expr.PropertyGetExpression;

public record ArrayTypeExpression(Expression parent) implements TypeExpression {
	public ArrayTypeExpression {
		if ((parent instanceof PropertyGetExpression getter && !getter.canBeUsedAsType()) &&
			!(parent instanceof TypeExpression))
			throw new RuntimeException("Can't use " + parent + " as type to make array variant");
	}

	@Override
	public String toString() {
		return parent + "[]";
	}
}
