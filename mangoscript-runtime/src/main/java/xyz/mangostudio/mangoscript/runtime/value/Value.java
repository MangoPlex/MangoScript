package xyz.mangostudio.mangoscript.runtime.value;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import xyz.mangostudio.mangoscript.binary.type.ArrayType;
import xyz.mangostudio.mangoscript.binary.type.OptionalType;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.factory.StringClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.number.F32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F64Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I16Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I64Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I8Value;
import xyz.mangostudio.mangoscript.runtime.value.number.NumberValue;
import xyz.mangostudio.mangoscript.runtime.value.object.StringValue;

public interface Value {
	public Type getType();

	default Value getProperty(String name) {
		throw new RuntimeException("Cannot get '" + name + "' property of " + this);
	}

	default void setProperty(String name, Value value) {
		throw new RuntimeException("Cannot set '" + name + "' property of " + this);
	}

	default Value call(Value... args) {
		throw new RuntimeException(this + " is not callable");
	}

	default Value castTo(Type targetType) {
		if (getType() == targetType) return this;
		if (targetType == StringClassRuntimeType.CLASS) return new StringValue(toString());
		throw new RuntimeException(this + " can't be casted to " + targetType);
	}

	@SuppressWarnings("unchecked")
	default <T> T castTo(Type targetType, Class<T> clazz) {
		return (T) castTo(targetType);
	}

	default Value add(Value another) {
		return getProperty("+").call(another);
	}

	default Value subtract(Value another) {
		return getProperty("-").call(another);
	}

	default Value multiply(Value another) {
		return getProperty("*").call(another);
	}

	default Value divide(Value another) {
		return getProperty("/").call(another);
	}

	default Value and(Value another) {
		return getProperty("&").call(another);
	}

	default Value or(Value another) {
		return getProperty("|").call(another);
	}

	default Value xor(Value another) {
		return getProperty("^").call(another);
	}

	default Value not() {
		return getProperty("unary !").call();
	}

	default Value negate() {
		return getProperty("unary -").call();
	}

	default Value inc() {
		return getProperty("unary ++").call();
	}

	default Value dec() {
		return getProperty("unary --").call();
	}

	default Value valueEquals(Value another) {
		return getProperty("==").call(another);
	}

	default Value greaterThan(Value another) {
		return getProperty(">").call(another);
	}

	default Value lessThan(Value another) {
		return getProperty("<").call(another);
	}

	default Value greaterThanOrEquals(Value another) {
		return getProperty(">=").call(another);
	}

	default Value lessThanOrEquals(Value another) {
		return getProperty("<=").call(another);
	}

	default Value shiftLeft(Value another) {
		return getProperty("<<").call(another);
	}

	default Value shiftRight(Value another) {
		return getProperty(">>").call(another);
	}

	default Value subscriptGet(Value... args) {
		throw new RuntimeException("Can't get subscript of " + this + ": ["
			+ Stream.of(args).map(Object::toString).collect(Collectors.joining(", ")) + "]");
	}

	default void subscriptSet(Value[] args, Value value) {
		throw new RuntimeException("Can't set subscript of " + this + ": ["
			+ Stream.of(args).map(Object::toString).collect(Collectors.joining(", ")) + "]");
	}

	default boolean asBoolean() {
		return this instanceof NumberValue n ? n.getJvmNumber().intValue() != 0
			: this instanceof OptionalValue opt ? !opt.isEmpty()
			: false;
	}

	public static Value createDefault(Type type) {
		if (type instanceof PrimitiveType pt) return switch (pt) {
		case I8 -> new I8Value((byte) 0);
		case I16 -> new I16Value((short) 0);
		case I32 -> new I32Value(0);
		case I64 -> new I64Value(0L);
		case F32 -> new F32Value(0f);
		case F64 -> new F64Value(0d);
		case ANY -> throw new RuntimeException("Can't create default value for 'any'");
		case VOID -> throw new RuntimeException("'void' can only be used as function result");
		default -> throw new RuntimeException("Not yet implemented: " + pt);
		};

		if (type instanceof OptionalType opt) return new OptionalValue(opt);
		if (type instanceof ArrayType arr) return new ArrayValue(arr, 0);
		if (type instanceof ClassRuntimeType rt) return rt.createDefault();
		throw new RuntimeException("Not yet implemented: " + type);
	}
}
