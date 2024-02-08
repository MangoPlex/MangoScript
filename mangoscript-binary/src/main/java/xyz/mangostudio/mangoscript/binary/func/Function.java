package xyz.mangostudio.mangoscript.binary.func;

/**
 * <p>
 * Function in MangoScript. In binary module, there is only a single type of
 * function available: {@link GuestFunction}. However, during runtime, there
 * could be another type of function, called "native function". Native functions
 * are bounds to runtime only and is the only way to interact with environment
 * outside MangoScript module.
 * </p>
 * <p>
 * See {@link FunctionSignature} for more information on function signatures.
 * </p>
 */
public interface Function {
	public FunctionSignature getFunctionSignature();
}
