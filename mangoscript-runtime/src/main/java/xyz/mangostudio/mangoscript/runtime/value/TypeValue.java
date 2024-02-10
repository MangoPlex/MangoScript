package xyz.mangostudio.mangoscript.runtime.value;

import xyz.mangostudio.mangoscript.binary.type.ArrayType;
import xyz.mangostudio.mangoscript.binary.type.OptionalType;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.type.RuntimeTypes;
import xyz.mangostudio.mangoscript.runtime.value.number.NumberValue;

public record TypeValue(Type targetType) implements Value {
	@Override
	public Type getType() { return PrimitiveType.TYPE; }

	@Override
	public Value call(Value... args) {
		if (targetType instanceof ClassRuntimeType rt) return rt.create(args);

		if (targetType instanceof OptionalType opt) {
			if (args.length == 0) return new OptionalValue(opt);
			if (args.length == 1)
				return new OptionalValue(RuntimeTypes.toPrimitivesIfPossible(opt.type()).optionalVariant(), args[0]);
		}

		if (targetType instanceof ArrayType arr) {
			if (args.length == 0) return new ArrayValue(arr, 0);
			if (args.length == 1 && args[0] instanceof NumberValue num)
				return new ArrayValue(arr, num.getJvmNumber().intValue());
		}

		return Value.super.call(args);
	}
}
