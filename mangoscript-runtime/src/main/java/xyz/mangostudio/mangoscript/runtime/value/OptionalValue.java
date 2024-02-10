package xyz.mangostudio.mangoscript.runtime.value;

import xyz.mangostudio.mangoscript.binary.type.OptionalType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.factory.FactoryModuleContext;
import xyz.mangostudio.mangoscript.runtime.factory.ObjectClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.type.ClassOfPrimitive;
import xyz.mangostudio.mangoscript.runtime.type.RuntimeTypes;
import xyz.mangostudio.mangoscript.runtime.value.hidden.TargetFunctionValue;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;

public record OptionalValue(OptionalType type, Value value) implements Value {
	public OptionalValue {
		if (value != null) {
			Type sourceType = value.getType();
			if (sourceType instanceof ClassOfPrimitive cop) sourceType = cop.getPrimitive();

			Type destType = type.type();
			if (destType instanceof ClassOfPrimitive cop) destType = cop.getPrimitive();

			if (sourceType != destType) throw new RuntimeException("Type mismatch: " + sourceType + " != " + destType);
		}
	}

	public OptionalValue(OptionalType type) {
		this(RuntimeTypes.toPrimitivesIfPossible(type.type()).optionalVariant(), null);
	}

	public boolean isEmpty() { return value == null; }

	@Override
	public Type getType() { return type; }

	public Value getOrThrow() {
		if (isEmpty()) throw new RuntimeException("Value is empty");
		return value;
	}

	@Override
	public Value getProperty(String name) {
		return switch (name) {
		case "toString" ->
			new TargetFunctionValue(FactoryModuleContext.FACTORY, this, ObjectClassRuntimeType.TOSTRING);
		case "castTo" -> new TargetFunctionValue(FactoryModuleContext.FACTORY, this, ObjectClassRuntimeType.CASTTO);
		case "isEmpty" -> isEmpty() ? I32Value.TRUE : I32Value.FALSE;
		case "value" -> getOrThrow();
		default -> Value.super.getProperty(name);
		};
	}

	@Override
	public String toString() {
		return type + "(" + (isEmpty() ? ")" : value + ")");
	}

	@Override
	public Value castTo(Type targetType) {
		if (targetType == type) return this;
		if (targetType == type.type()) return getOrThrow();
		return Value.super.castTo(targetType);
	}
}
