package xyz.mangostudio.mangoscript.binary.expr;

public enum Operator {
	EMPTY_COALESCING("??", 0),

	AND("&", 1),
	OR("|", 1),
	XOR("^", 1),

	SHIFT_LEFT("<<", 2),
	SHIFT_RIGHT(">>", 2),

	MULTIPLY("*", 3),
	DIVIDE("/", 3),

	ADD("+", 4),
	SUBTRACT("-", 4),

	LESS_THAN("<", 5),
	GREATER_THAN(">", 5),
	LESS_THAN_OR_EQUALS("<=", 5),
	GREATER_THAN_OR_EQUALS(">=", 5),
	EQUALS("==", 5);

	public static final Operator[][] ORDER_OF_OPERATIONS = {
		{ EMPTY_COALESCING },
		{ AND, OR, XOR },
		{ SHIFT_LEFT, SHIFT_RIGHT },
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
