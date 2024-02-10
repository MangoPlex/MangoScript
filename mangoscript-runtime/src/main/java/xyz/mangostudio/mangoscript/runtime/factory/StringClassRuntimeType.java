package xyz.mangostudio.mangoscript.runtime.factory;

import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;
import xyz.mangostudio.mangoscript.runtime.value.object.StringValue;

public class StringClassRuntimeType implements ClassRuntimeType {
	public static final StringClassRuntimeType CLASS = new StringClassRuntimeType();

	@Override
	public ModuleContext getModule() { return FactoryModuleContext.FACTORY; }

	@Override
	public ClassRuntimeType getSuperclass() { return ObjectClassRuntimeType.CLASS; }

	@Override
	public StringValue createDefault() {
		return new StringValue();
	}

	@Override
	public Value getPropertyOf(Value target, String name) {
		return switch (name) {
		case "length" -> new I32Value(((StringValue) target).getContent().length());
		default -> ClassRuntimeType.super.getPropertyOf(target, name);
		};
	}

	@Override
	public String toString() {
		return "string";
	}
}
