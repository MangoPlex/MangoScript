package xyz.mangostudio.mangoscript.binary.expr;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record InvokeExpression(Expression target, Expression... args) implements Expression {
	@Override
	public String toString() {
		return target + "(" + Stream.of(args).map(Object::toString).collect(Collectors.joining(", ")) + ")";
	}
}
