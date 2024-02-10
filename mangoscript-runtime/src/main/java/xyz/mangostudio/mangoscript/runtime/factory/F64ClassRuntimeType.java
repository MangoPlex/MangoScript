package xyz.mangostudio.mangoscript.runtime.factory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.type.ClassOfPrimitive;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F64Value;

public final class F64ClassRuntimeType implements ClassRuntimeType, ClassOfPrimitive {
	public static final F64ClassRuntimeType CLASS = new F64ClassRuntimeType();

	@Override
	public ModuleContext getModule() { return FactoryModuleContext.FACTORY; }

	@Override
	public ClassRuntimeType getSuperclass() { return ObjectClassRuntimeType.CLASS; }

	@Override
	public F64Value createDefault() {
		return new F64Value(0);
	}

	@Override
	public Value create(Value... args) {
		if (args.length == 0) return createDefault();
		if (args.length == 1) return args[0].castTo(PrimitiveType.F64);

		throw new RuntimeException("Constructor call for f64 failed: "
			+ Stream.of(args).map(Object::toString).collect(Collectors.joining(", ")));
	}

	@Override
	public String toString() {
		return "f64";
	}

	@Override
	public PrimitiveType getPrimitive() { return PrimitiveType.F64; }
}
