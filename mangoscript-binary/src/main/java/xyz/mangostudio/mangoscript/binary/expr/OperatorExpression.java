package xyz.mangostudio.mangoscript.binary.expr;

public record OperatorExpression(Expression left, Expression right, Operator operator) implements Expression {
	private String asUngrouped() {
		if (left instanceof OperatorExpression leftOpExp
			&& leftOpExp.operator.getOrderOfOperation() == operator.getOrderOfOperation()) {
			return leftOpExp.asUngrouped() + " " + operator + " " + right;
		}

		return left + " " + operator + " " + right;
	}

	@Override
	public String toString() {
		return "(" + asUngrouped() + ")";
	}
}
