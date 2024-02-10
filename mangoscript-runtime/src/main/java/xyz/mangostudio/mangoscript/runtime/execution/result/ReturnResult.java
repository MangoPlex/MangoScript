package xyz.mangostudio.mangoscript.runtime.execution.result;

import xyz.mangostudio.mangoscript.runtime.value.Value;

public record ReturnResult(Value value) implements ExecutionResult {
}
