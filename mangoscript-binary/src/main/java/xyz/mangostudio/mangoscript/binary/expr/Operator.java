package xyz.mangostudio.mangoscript.binary.expr;

public enum Operator {
	ADD("+", 2),
	SUBTRACT("-", 2),
	MULTIPLY("*", 1),
	DIVIDE("/", 1),
	AND("&", 0),
	OR("|", 0),
	XOR("^", 0),
	LESS_THAN("<", 3),
	GREATER_THAN(">", 3),
	LESS_THAN_OR_EQUALS("<=", 3),
	GREATER_THAN_OR_EQUALS(">=", 3),
	EQUALS("==", 3);

	public static final Operator[][] ORDER_OF_OPERATIONS = {
		{ AND, OR, XOR },
		{ MULTIPLY, DIVIDE },
		{ ADD, SUBTRACT },
		{ LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUALS, GREATER_THAN_OR_EQUALS, EQUALS }
	};

	private String operatorSymbol;
	private int orderOfOperation;

	private Operator(String operatorSymbol, int orderOfOperation) {
		this.operatorSymbol = operatorSymbol;
		this.orderOfOperation = orderOfOperation;
	}

	public String getOperatorSymbol() { return operatorSymbol; }

	public int getOrderOfOperation() { return orderOfOperation; }

	@Override
	public String toString() {
		return operatorSymbol;
	}
}
