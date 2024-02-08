package xyz.mangostudio.mangoscript.binary.expr;

public record PropertyUnaryModifyExpression(Expression target, String name, Unary type) implements Expression {
	@Override
	public String toString() {
		String target = this.target == LocalExpression.LOCAL ? name : this.target + "." + name;
		return switch (type) {
		case ADD_BEFORE -> "++" + target;
		case ADD_AFTER -> target + "++";
		case SUBTRACT_BEFORE -> "--" + target;
		case SUBTRACT_AFTER -> target + "--";
		default -> throw new IllegalStateException();
		};
	}
}
