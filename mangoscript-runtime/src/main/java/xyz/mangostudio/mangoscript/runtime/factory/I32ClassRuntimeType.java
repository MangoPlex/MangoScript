package xyz.mangostudio.mangoscript.runtime.factory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.type.ClassOfPrimitive;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;

public final class I32ClassRuntimeType implements ClassRuntimeType, ClassOfPrimitive {
	public static final I32ClassRuntimeType CLASS = new I32ClassRuntimeType();

	@Override
	public ModuleContext getModule() { return FactoryModuleContext.FACTORY; }

	@Override
	public ClassRuntimeType getSuperclass() { return ObjectClassRuntimeType.CLASS; }

	@Override
	public I32Value createDefault() {
		return new I32Value(0);
	}

	@Override
	public Value create(Value... args) {
		if (args.length == 0) return createDefault();
		if (args.length == 1) return args[0].castTo(PrimitiveType.I32);

		throw new RuntimeException("Constructor call for i32 failed: "
			+ Stream.of(args).map(Object::toString).collect(Collectors.joining(", ")));
	}

	@Override
	public String toString() {
		return "i32";
	}

	@Override
	public PrimitiveType getPrimitive() { return PrimitiveType.I32; }
}
