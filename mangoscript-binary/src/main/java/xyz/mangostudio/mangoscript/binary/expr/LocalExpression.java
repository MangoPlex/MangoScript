package xyz.mangostudio.mangoscript.binary.expr;

public class LocalExpression implements Expression {
	public static final LocalExpression LOCAL = new LocalExpression();

	private LocalExpression() {}

	@Override
	public String toString() {
		return "(local)";
	}
}
