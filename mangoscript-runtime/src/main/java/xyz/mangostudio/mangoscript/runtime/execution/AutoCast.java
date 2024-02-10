package xyz.mangostudio.mangoscript.runtime.execution;

import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.type.OptionalType;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.type.RuntimeTypes;
import xyz.mangostudio.mangoscript.runtime.value.OptionalValue;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.object.ObjectValue;

/**
 * <p>
 * Auto cast follows the auto casting rule defined in <cite>MangoScript Runtime
 * Specification</cite>.
 * </p>
 */
public class AutoCast {
	public static Value toPrimitive(PrimitiveType destType, Value source) {
		if (destType == PrimitiveType.VOID) {
			if (source != null) throw new RuntimeException("void can't return a value");
			return null;
		}

		if (source.getType() == destType || destType == PrimitiveType.ANY) return source;
		if (!(source.getType() instanceof PrimitiveType sourceType))
			throw new RuntimeException("Source type is not primitive");

		int sourceLevel = getPrimitiveCastLevel(sourceType);
		int destLevel = getPrimitiveCastLevel(destType);
		if (sourceLevel > destLevel) throw new RuntimeException("Can't autocast from " + sourceType + " to " + destType
			+ ", explict casting required");
		return source.castTo(destType);
	}

	public static int getPrimitiveCastLevel(PrimitiveType type) {
		return switch (type) {
		case I8 -> 0;
		case I16 -> 1;
		case I32 -> 2;
		case I64 -> 3;
		case F32 -> 4;
		case F64 -> 5;
		default -> throw new RuntimeException("Unknown type: " + type);
		};
	}

	/**
	 * <p>
	 * Apply auto-casting rule on source value. This method throws if explict
	 * casting is required.
	 * </p>
	 * 
	 * @param destType The target type that you want.
	 * @param source   The source value to cast.
	 * @return Value with casted type. If the value is {@link ObjectValue}, the
	 *         runtime type could be different.
	 */
	public static Value to(Type destType, Value source) {
		destType = RuntimeTypes.toPrimitivesIfPossible(destType);
		Type sourceType = RuntimeTypes.toPrimitivesIfPossible(source.getType());

		if (destType == PrimitiveType.ANY || destType == sourceType) return source;
		if (destType instanceof PrimitiveType pt) return toPrimitive(pt, source);

		if (destType instanceof OptionalType destOptType) {
			if (source instanceof OptionalValue sourceOpt) {
				if (sourceOpt.isEmpty()) return new OptionalValue(destOptType);
				return new OptionalValue(destOptType, to(destOptType.type(), sourceOpt.value()));
			}

			return new OptionalValue(destOptType, source);
		}

		if (source instanceof OptionalValue sourceOpt) return to(destType, sourceOpt.getOrThrow());

		throw new RuntimeException("Can't autocast from " + sourceType + " to " + destType
			+ ", explict casting required");
	}

	/**
	 * <p>
	 * Cast an array of function inputs into another array of function inputs, but
	 * with its type casted to correct type specified in
	 * {@link FunctionSignature#parameters()}.
	 * </p>
	 * 
	 * @param signature The function signature.
	 * @param args      An array of function inputs.
	 * @return A new array of function inputs.
	 */
	public static Value[] castToFunctionSignature(FunctionSignature signature, Value... args) {
		if (args.length > signature.parameters().length)
			throw new RuntimeException("Number of inputs exceeds function signature's parameters count: "
				+ args.length + " > " + signature.parameters().length);

		Value[] out = new Value[signature.parameters().length];

		for (int i = 0; i < out.length; i++) {
			Type paramType = signature.parameters()[i];

			if (i < args.length) out[i] = to(paramType, args[i]);
			else if (paramType instanceof OptionalType opt) out[i] = Value.createDefault(opt);
			else throw new RuntimeException("Parameter at #" + i + " of signature is not optional");
		}

		return out;
	}
}
