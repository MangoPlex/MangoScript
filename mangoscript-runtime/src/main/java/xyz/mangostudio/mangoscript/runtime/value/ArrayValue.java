package xyz.mangostudio.mangoscript.runtime.value;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.type.ArrayType;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.factory.FactoryModuleContext;
import xyz.mangostudio.mangoscript.runtime.factory.ObjectClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.host.NativeFunction;
import xyz.mangostudio.mangoscript.runtime.type.RuntimeTypes;
import xyz.mangostudio.mangoscript.runtime.value.hidden.TargetFunctionValue;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.NumberValue;

public class ArrayValue implements Value {
	private ArrayType type;
	private Value[] internal;

	// TODO
	// In the future, ArrayValue can stores byte[], short[], int[], long[] float[]
	// and double[] if the base type is a primitive. This helps keeping the memory
	// usage low for big array of primitives.

	public ArrayValue(ArrayType type, Value[] values) {
		this.type = type;
		this.internal = values;
	}

	public ArrayValue(ArrayType type, int length) {
		this.type = RuntimeTypes.toPrimitivesIfPossible(type.base()).arrayVariant();
		this.internal = new Value[length];
		for (int i = 0; i < length; i++) internal[i] = Value.createDefault(type.base());
	}

	@Override
	public ArrayType getType() { return type; }

	/**
	 * <p>
	 * Get the internal backing array.
	 * </p>
	 * 
	 * @return The internal backing array.
	 * @deprecated This method is marked as deprecated because the implementation
	 *             could be changed in the future to support a tighter packing of
	 *             primitive arrays, which helps reducing memory usage.
	 */
	@Deprecated
	public Value[] getInternal() { return internal; }

	public int size() {
		return internal.length;
	}

	@Override
	public String toString() {
		return type + ": [" + Stream.of(internal).map(Object::toString).collect(Collectors.joining(", ")) + "]";
	}

	@Override
	public Value getProperty(String name) {
		return switch (name) {
		case "toString" ->
			new TargetFunctionValue(FactoryModuleContext.FACTORY, this, ObjectClassRuntimeType.TOSTRING);
		case "castTo" -> new TargetFunctionValue(FactoryModuleContext.FACTORY, this, ObjectClassRuntimeType.CASTTO);
		case "length" -> new I32Value(size());
		case "resizeTo" -> new TargetFunctionValue(FactoryModuleContext.FACTORY, this, new NativeFunction() {
			@Override
			public FunctionSignature getFunctionSignature() {
				return new FunctionSignature(type, PrimitiveType.I32);
			}

			@Override
			public Value callNative(Value thisValue, Value... args) {
				Value[] resized = new Value[args[0].castTo(PrimitiveType.I32, I32Value.class).value()];
				System.arraycopy(internal, 0, resized, 0, Math.min(internal.length, resized.length));
				return new ArrayValue(type, resized);
			}
		});
		default -> Value.super.getProperty(name);
		};
	}

	@Override
	public Value subscriptGet(Value... args) {
		if (args.length != 1 || !(args[0] instanceof NumberValue num)) return Value.super.subscriptGet(args);
		return internal[num.getJvmNumber().intValue()];
	}

	@Override
	public void subscriptSet(Value[] args, Value value) {
		if (args.length != 1 || !(args[0] instanceof NumberValue num)) Value.super.subscriptSet(args, value);
		else internal[num.getJvmNumber().intValue()] = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ArrayValue another)) return false;
		if (!getType().equals(another.getType())) return false;
		if (size() != another.size()) return false;
		for (int i = 0; i < size(); i++) if (!internal[i].equals(another.internal[i])) return false;
		return true;
	}
}
