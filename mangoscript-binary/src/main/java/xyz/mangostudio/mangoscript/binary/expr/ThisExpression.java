package xyz.mangostudio.mangoscript.binary.expr;

public class ThisExpression implements Expression {
	public static final ThisExpression THIS = new ThisExpression();

	private ThisExpression() {}

	@Override
	public String toString() {
		return "this";
	}
}
