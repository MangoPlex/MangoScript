package xyz.mangostudio.mangoscript.runtime.host;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public interface NativeFunction extends Function {
	public Value callNative(Value thisValue, Value... args);
}
