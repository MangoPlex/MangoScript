package xyz.mangostudio.mangoscript.binary.expr;

public record UnaryExpression(Expression expression, Unary type) implements Expression {
	@Override
	public String toString() {
		return switch (type) {
		case NEGATE -> "-" + expression;
		case NOT -> "!" + expression;
		default -> throw new IllegalStateException();
		};
	}
}
