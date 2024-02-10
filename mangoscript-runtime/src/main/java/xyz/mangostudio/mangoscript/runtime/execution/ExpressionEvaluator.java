package xyz.mangostudio.mangoscript.runtime.execution;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.runtime.value.Value;

@FunctionalInterface
public interface ExpressionEvaluator<T extends Expression> {
	public Value evaluate(ExecutionContext context, T expression);
}
