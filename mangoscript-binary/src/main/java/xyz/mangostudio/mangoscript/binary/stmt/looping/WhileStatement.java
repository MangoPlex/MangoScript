package xyz.mangostudio.mangoscript.binary.stmt.looping;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;

public record WhileStatement(Expression condition, Statement whileTrue) implements LoopingStatement {
	@Override
	public String toString() {
		return "while (" + condition + ") " + whileTrue;
	}
}
