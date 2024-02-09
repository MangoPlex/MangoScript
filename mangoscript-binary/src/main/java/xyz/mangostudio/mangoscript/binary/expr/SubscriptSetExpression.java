package xyz.mangostudio.mangoscript.binary.expr;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record SubscriptSetExpression(Expression target, Expression[] args, Expression expression) implements Expression {
	@Override
	public String toString() {
		return target + "[" + Stream.of(args).map(Object::toString).collect(Collectors.joining(", ")) + "] = "
			+ expression;
	}
}
