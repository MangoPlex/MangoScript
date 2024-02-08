package xyz.mangostudio.mangoscript.binary.stmt.looping;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;

/**
 * <p>
 * Family of looping statements must implement this interface. Use with
 * {@link LabeledLoopingStatement} to assign a label, whose loop can be broken
 * by {@link BreakStatement}.
 * </p>
 */
public interface LoopingStatement extends Statement {
}
