package xyz.mangostudio.mangoscript.binary.expr.literal;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;

public record LiteralNumberExpression(PrimitiveType type, long value) implements Expression {
	public LiteralNumberExpression(byte b) {
		this(PrimitiveType.I8, Byte.toUnsignedLong(b));
	}

	public LiteralNumberExpression(short s) {
		this(PrimitiveType.I16, Short.toUnsignedLong(s));
	}

	public LiteralNumberExpression(int i) {
		this(PrimitiveType.I32, Integer.toUnsignedLong(i));
	}

	public LiteralNumberExpression(long l) {
		this(PrimitiveType.I64, l);
	}

	public LiteralNumberExpression(float f) {
		this(PrimitiveType.F32, Float.floatToRawIntBits(f));
	}

	public LiteralNumberExpression(double d) {
		this(PrimitiveType.F32, Double.doubleToRawLongBits(d));
	}

	public byte asByte() {
		return (byte) value;
	}

	public short asShort() {
		return (short) value;
	}

	public int asInt() {
		return (int) value;
	}

	public float asFloat() {
		return Float.intBitsToFloat((int) value);
	}

	public double asDouble() {
		return Double.longBitsToDouble(value);
	}

	public LiteralNumberExpression negate() {
		return switch (type) {
		case I8 -> new LiteralNumberExpression((byte) -asByte());
		case I16 -> new LiteralNumberExpression((short) -asShort());
		case I32 -> new LiteralNumberExpression(-asInt());
		case I64 -> new LiteralNumberExpression(-value);
		case F32 -> new LiteralNumberExpression(-asFloat());
		case F64 -> new LiteralNumberExpression(-asDouble());
		default -> throw new IllegalStateException();
		};
	}

	@Override
	public String toString() {
		return switch (type) {
		case I8 -> asByte() + "b";
		case I16 -> asShort() + "s";
		case I32 -> asInt() + "i";
		case I64 -> value + "l";
		case F32 -> asFloat() + "f";
		case F64 -> asDouble() + "d";
		default -> throw new IllegalStateException();
		};
	}
}
