package xyz.mangostudio.mangoscript.runtime.value.hidden;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.execution.Executor;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public record TargetFunctionValue(ModuleContext module, Value target, Function function) implements FunctionValue {
	@Override
	public FunctionSignature getSignature() { return function.getFunctionSignature(); }

	@Override
	public Type getType() { return null; }

	@Override
	public Value call(Value... args) {
		return Executor.executeFunction(module.newExecutionOf(target), function, args);
	}
}
