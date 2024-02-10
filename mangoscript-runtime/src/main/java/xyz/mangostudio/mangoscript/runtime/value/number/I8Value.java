package xyz.mangostudio.mangoscript.runtime.value.number;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.object.StringValue;

public record I8Value(byte value) implements NumberValue {
	@Override
	public PrimitiveType getType() { return PrimitiveType.I8; }

	@Override
	public Number getJvmNumber() { return value; }

	@Override
	public Value add(Value another) {
		if (another instanceof StringValue s) return new StringValue(value + s.getContent());
		if (another instanceof I8Value a) return new I8Value((byte) (value + a.value));
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].add(a[1]);
	}

	@Override
	public Value subtract(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value - a.value));
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].subtract(a[1]);
	}

	@Override
	public Value multiply(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value * a.value));
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].multiply(a[1]);
	}

	@Override
	public Value divide(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value / a.value));
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].divide(a[1]);
	}

	@Override
	public Value and(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value & a.value));
		NumberValue[] a = NumberValue.castForIntegerOperators(this, another);
		return a[0].and(a[1]);
	}

	@Override
	public Value or(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value | a.value));
		NumberValue[] a = NumberValue.castForIntegerOperators(this, another);
		return a[0].or(a[1]);
	}

	@Override
	public Value xor(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value ^ a.value));
		NumberValue[] a = NumberValue.castForIntegerOperators(this, another);
		return a[0].xor(a[1]);
	}

	@Override
	public Value shiftLeft(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value << a.value));
		NumberValue[] a = NumberValue.castForIntegerOperators(this, another);
		return a[0].shiftLeft(a[1]);
	}

	@Override
	public Value shiftRight(Value another) {
		if (another instanceof I8Value a) return new I8Value((byte) (value >> a.value));
		NumberValue[] a = NumberValue.castForIntegerOperators(this, another);
		return a[0].shiftRight(a[1]);
	}

	@Override
	public Value lessThan(Value another) {
		if (another instanceof I8Value a) return new I32Value(value < a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].lessThan(a[1]);
	}

	@Override
	public Value lessThanOrEquals(Value another) {
		if (another instanceof I8Value a) return new I32Value(value <= a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].lessThanOrEquals(a[1]);
	}

	@Override
	public Value greaterThan(Value another) {
		if (another instanceof I8Value a) return new I32Value(value > a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].greaterThan(a[1]);
	}

	@Override
	public Value greaterThanOrEquals(Value another) {
		if (another instanceof I8Value a) return new I32Value(value >= a.value ? 1 : 0);
		NumberValue[] a = NumberValue.castForMathOperators(this, another);
		return a[0].greaterThanOrEquals(a[1]);
	}

	@Override
	public Value not() {
		return new I8Value(value == 0 ? (byte) 1 : (byte) 0);
	}

	@Override
	public Value negate() {
		return new I8Value((byte) (-value));
	}

	@Override
	public Value inc() {
		return new I8Value((byte) (value + 1));
	}

	@Override
	public Value dec() {
		return new I8Value((byte) (value - 1));
	}

	@Override
	public String toString() {
		return getJvmNumber().toString();
	}
}
