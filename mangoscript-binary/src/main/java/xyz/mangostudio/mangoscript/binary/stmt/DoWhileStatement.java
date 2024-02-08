package xyz.mangostudio.mangoscript.binary.stmt;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.looping.LoopingStatement;

public record DoWhileStatement(Statement whileTrue, Expression condition) implements LoopingStatement {
	@Override
	public String toString() {
		return "do " + whileTrue + " while (" + condition + ");";
	}
}
