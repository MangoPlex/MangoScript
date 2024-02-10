package xyz.mangostudio.mangoscript.runtime.factory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.host.NativeFunction;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.TypeValue;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.hidden.TargetFunctionValue;

public class ObjectClassRuntimeType implements ClassRuntimeType {
	public static final ObjectClassRuntimeType CLASS = new ObjectClassRuntimeType();
	public static final NativeFunction TOSTRING = new NativeFunction() {
		@Override
		public FunctionSignature getFunctionSignature() { return new FunctionSignature(StringClassRuntimeType.CLASS); }

		@Override
		public Value callNative(Value thisValue, Value... args) {
			return thisValue.castTo(StringClassRuntimeType.CLASS);
		}
	};
	public static final NativeFunction CASTTO = new NativeFunction() {
		@Override
		public FunctionSignature getFunctionSignature() {
			return new FunctionSignature(PrimitiveType.ANY, PrimitiveType.TYPE);
		}

		@Override
		public Value callNative(Value thisValue, Value... args) {
			return thisValue.castTo(args[0].castTo(PrimitiveType.TYPE, TypeValue.class).targetType());
		}
	};

	@Override
	public ModuleContext getModule() { return FactoryModuleContext.FACTORY; }

	@Override
	public ClassRuntimeType getSuperclass() { return this; }

	@Override
	public List<Map.Entry<String, Type>> getAllFields() { return Collections.emptyList(); }

	@Override
	public Value getPropertyOf(Value target, String name) {
		return switch (name) {
		case "toString" -> new TargetFunctionValue(getModule(), target, TOSTRING);
		case "castTo" -> new TargetFunctionValue(getModule(), target, CASTTO);
		default -> null;
		};
	}

	@Override
	public String toString() {
		return "object";
	}
}
