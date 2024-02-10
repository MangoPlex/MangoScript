package xyz.mangostudio.mangoscript.runtime.execution;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.runtime.execution.result.ExecutionResult;

@FunctionalInterface
public interface StatementExecutor<T extends Statement> {
	public ExecutionResult execute(ExecutionContext context, T statement);
}
