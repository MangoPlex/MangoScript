package xyz.mangostudio.mangoscript.binary.func;

import xyz.mangostudio.mangoscript.binary.type.OptionalType;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;

/**
 * <p>
 * A function signature describe the function inputs and outputs. A typical
 * function can accept multiple inputs, but can only return 0 or 1 output. If
 * the output type is {@link PrimitiveType#VOID}, that means the function does
 * not return anything (and using the function as expression will raise runtime
 * error).
 * </p>
 * <p>
 * <b>Partial parameters</b>: To allow users to call the function with partial
 * number of parameters, the last few parameter types can be
 * {@link OptionalType}. When calling the function with partial parameters, the
 * parameters that aren't provided will have {@code empty} variant. For example,
 * a function {@code void(i32, i32?)} can be used as {@code void(i32, i32)} or
 * {@code void(i32)}.
 * </p>
 */
public record FunctionSignature(Type output, Type... parameters) {
	public FunctionSignature {
		if (output == null)
			throw new NullPointerException("output type can't be null (use PrimitiveType.VOID instead)");

		boolean inPartialMode = false;

		for (Type p : parameters) {
			if (p instanceof OptionalType) {
				inPartialMode = true;
				continue;
			} else if (inPartialMode) {
				throw new IllegalArgumentException("Cannot have optional type between non-optional types");
			}
		}
	}

	public static FunctionSignature voidFunction(Type... parameters) {
		return new FunctionSignature(PrimitiveType.VOID, parameters);
	}

	@Override
	public String toString() {
		String content = "";
		for (Type param : parameters) content += (content.length() == 0 ? "" : ", ") + param;
		return output + "(" + content + ")";
	}
}
