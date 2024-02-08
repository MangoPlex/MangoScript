package xyz.mangostudio.mangoscript.binary.stmt;

import xyz.mangostudio.mangoscript.binary.expr.Expression;

public record ConditionalStatement(Expression condition, Statement ifTrue, Statement ifFalse) implements Statement {
	public ConditionalStatement(Expression condition, Statement ifTrue) {
		this(condition, ifTrue, null);
	}

	@Override
	public String toString() {
		String content = "if (" + condition + ") " + ifTrue;
		if (ifFalse != null) content += " else " + ifFalse;
		return content;
	}
}
