package xyz.mangostudio.mangoscript.binary.expr;

public enum BoolExpression implements Expression {
	TRUE,
	FALSE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
