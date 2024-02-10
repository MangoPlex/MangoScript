package xyz.mangostudio.mangoscript.runtime.value.number;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public sealed interface NumberValue extends Value permits I8Value, I16Value, I32Value, I64Value, F32Value, F64Value {
	@Override
	public PrimitiveType getType();

	public Number getJvmNumber();

	public static int typeToLevel(PrimitiveType type) {
		return switch (type) {
		case I8 -> 0;
		case I16 -> 1;
		case I32 -> 2;
		case I64 -> 3;
		case F32 -> 4;
		case F64 -> 5;
		default -> throw new RuntimeException("Type not supported: " + type);
		};
	}

	public static PrimitiveType levelToType(int level) {
		return switch (level) {
		case 0 -> PrimitiveType.I8;
		case 1 -> PrimitiveType.I16;
		case 2 -> PrimitiveType.I32;
		case 3 -> PrimitiveType.I64;
		case 4 -> PrimitiveType.F32;
		case 5 -> PrimitiveType.F64;
		default -> throw new RuntimeException("Unknown level: " + level);
		};
	}

	public static NumberValue[] castForMathOperators(Value a, Value b) {
		if (!(a instanceof NumberValue aa)) throw new RuntimeException(a + " is not a number");
		if (!(b instanceof NumberValue bb)) throw new RuntimeException(b + " is not a number");
		PrimitiveType targetType = levelToType(Math.max(typeToLevel(aa.getType()), typeToLevel(bb.getType())));
		return new NumberValue[] { (NumberValue) a.castTo(targetType), (NumberValue) b.castTo(targetType) };
	}

	public static NumberValue[] castForIntegerOperators(Value a, Value b) {
		if (a.getType() == PrimitiveType.F32 || a.getType() == PrimitiveType.F64)
			throw new RuntimeException(a + " is not an integer");
		if (b.getType() == PrimitiveType.F32 || b.getType() == PrimitiveType.F64)
			throw new RuntimeException(b + " is not an integer");
		return castForMathOperators(a, b);
	}

	@Override
	default Value castTo(Type targetType) {
		if (targetType instanceof PrimitiveType pt) return switch (pt) {
		case I32 -> new I32Value(getJvmNumber().intValue());
		case F32 -> new F32Value(getJvmNumber().floatValue());
		default -> Value.super.castTo(targetType);
		};

		return Value.super.castTo(targetType);
	}

	@Override
	default Value valueEquals(Value another) {
		return equals(another) ? I32Value.TRUE : I32Value.FALSE;
	}
}
