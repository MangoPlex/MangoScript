package xyz.mangostudio.mangoscript.runtime.value.number;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.object.StringValue;

public record F64Value(double value) implements NumberValue {
	@Override
	public PrimitiveType getType() { return PrimitiveType.F64; }

	@Override
	public Number getJvmNumber() { return value; }

	@Override
	public Value add(Value another) {
		if (another instanceof StringValue s) return new StringValue(value + s.getContent());
		if (another instanceof F64Value a) return new F64Value(value + a.value);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].add(a[1]);
	}

	@Override
	public Value subtract(Value another) {
		if (another instanceof F64Value a) return new F64Value(value - a.value);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].subtract(a[1]);
	}

	@Override
	public Value multiply(Value another) {
		if (another instanceof F64Value a) return new F64Value(value * a.value);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].multiply(a[1]);
	}

	@Override
	public Value divide(Value another) {
		if (another instanceof F64Value a) return new F64Value(value / a.value);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].divide(a[1]);
	}

	@Override
	public Value lessThan(Value another) {
		if (another instanceof F64Value a) return new I32Value(value < a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].lessThan(a[1]);
	}

	@Override
	public Value lessThanOrEquals(Value another) {
		if (another instanceof F64Value a) return new I32Value(value <= a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].lessThanOrEquals(a[1]);
	}

	@Override
	public Value greaterThan(Value another) {
		if (another instanceof F64Value a) return new I32Value(value > a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].greaterThan(a[1]);
	}

	@Override
	public Value greaterThanOrEquals(Value another) {
		if (another instanceof F64Value a) return new I32Value(value >= a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].greaterThanOrEquals(a[1]);
	}

	@Override
	public Value negate() {
		return new F64Value(-value);
	}

	@Override
	public Value inc() {
		return new F64Value(value + 1);
	}

	@Override
	public Value dec() {
		return new F64Value(value - 1);
	}

	@Override
	public String toString() {
		return getJvmNumber().toString();
	}
}
