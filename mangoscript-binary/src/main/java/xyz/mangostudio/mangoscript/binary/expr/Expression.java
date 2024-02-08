package xyz.mangostudio.mangoscript.binary.expr;

public interface Expression {
	default OperatorExpression op(Operator operator, Expression right) {
		return new OperatorExpression(this, right, operator);
	}

	default InvokeExpression call(Expression... args) {
		return new InvokeExpression(this, args);
	}

	default PropertyGetExpression getProperty(String name) {
		return new PropertyGetExpression(this, name);
	}
}
