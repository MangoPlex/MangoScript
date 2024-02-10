package xyz.mangostudio.mangoscript.runtime.factory;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.type.ClassOfPrimitive;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I8Value;

public final class I8ClassRuntimeType implements ClassRuntimeType, ClassOfPrimitive {
	public static final I8ClassRuntimeType CLASS = new I8ClassRuntimeType();

	@Override
	public ModuleContext getModule() { return FactoryModuleContext.FACTORY; }

	@Override
	public ClassRuntimeType getSuperclass() { return ObjectClassRuntimeType.CLASS; }

	@Override
	public I8Value createDefault() {
		return new I8Value((byte) 0);
	}

	@Override
	public Value create(Value... args) {
		if (args.length == 0) return createDefault();
		if (args.length == 1) return args[0].castTo(PrimitiveType.I8);

		throw new RuntimeException("Constructor call for i8 failed: "
			+ Stream.of(args).map(Object::toString).collect(Collectors.joining(", ")));
	}

	@Override
	public String toString() {
		return "i8";
	}

	@Override
	public PrimitiveType getPrimitive() { return PrimitiveType.I8; }
}
