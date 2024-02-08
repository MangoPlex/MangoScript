package xyz.mangostudio.mangoscript.binary.stmt.looping;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;

/**
 * <p>
 * A {@code for (initialStatement; condition; nextExpression)} loop. The runtime
 * will call initial statement first, then while the condition is true, it will
 * run the {@link #whileTrue} statement and then evaluate {@link #next}
 * expression. The {@link #next} expression will not be used to determine when
 * to stop; use {@link #next} instead.
 * </p>
 */
public record ForStatement(Statement initial, Expression condition, Expression next, Statement whileTrue) implements LoopingStatement {
	@Override
	public String toString() {
		return "for ("
			+ (initial != null ? initial : ";")
			+ (condition != null ? " " + condition + ";" : ";")
			+ (next != null ? " " + next : "") + ") " + whileTrue;
	}
}
