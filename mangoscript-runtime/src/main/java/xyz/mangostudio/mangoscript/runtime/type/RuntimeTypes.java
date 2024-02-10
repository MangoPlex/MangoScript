package xyz.mangostudio.mangoscript.runtime.type;

import xyz.mangostudio.mangoscript.binary.type.ArrayType;
import xyz.mangostudio.mangoscript.binary.type.OptionalType;
import xyz.mangostudio.mangoscript.binary.type.Type;

public class RuntimeTypes {
	public static Type toPrimitivesIfPossible(Type source) {
		if (source instanceof ClassOfPrimitive cop) return cop.getPrimitive();
		if (source instanceof OptionalType opt) return new OptionalType(toPrimitivesIfPossible(opt.type()));
		if (source instanceof ArrayType arr) return new ArrayType(toPrimitivesIfPossible(arr.base()));
		return source;
	}
}
