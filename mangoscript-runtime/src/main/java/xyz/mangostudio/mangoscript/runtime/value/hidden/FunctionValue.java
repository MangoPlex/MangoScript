package xyz.mangostudio.mangoscript.runtime.value.hidden;

import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public interface FunctionValue extends Value {
	public FunctionSignature getSignature();

	@Override
	public Value call(Value... args);
}
