package xyz.mangostudio.mangoscript.runtime.factory;

import java.util.Map;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.binary.module.MangoScriptModule;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.module.ModuleResolver;
import xyz.mangostudio.mangoscript.runtime.module.SimpleModuleContext;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public class FactoryModuleContext extends SimpleModuleContext {
	public static final FactoryModuleContext FACTORY = new FactoryModuleContext();

	private FactoryModuleContext() {
		super.defineClass("object", ObjectClassRuntimeType.CLASS);
		super.defineClass("string", StringClassRuntimeType.CLASS);
		super.defineClass("i8", I8ClassRuntimeType.CLASS);
		super.defineClass("i16", I16ClassRuntimeType.CLASS);
		super.defineClass("i32", I32ClassRuntimeType.CLASS);
		super.defineClass("i64", I64ClassRuntimeType.CLASS);
		super.defineClass("f32", F32ClassRuntimeType.CLASS);
		super.defineClass("f64", F64ClassRuntimeType.CLASS);
	}

	@Override
	public void defineClass(String name, ClassRuntimeType objectClass) {
		throw new IllegalStateException("Can't define new symbol to factory module");
	}

	@Override
	public void defineFunction(String name, Function function) {
		throw new IllegalStateException("Can't define new symbol to factory module");
	}

	@Override
	public void defineConstant(String name, Value value) {
		throw new IllegalStateException("Can't define new symbol to factory module");
	}

	@Override
	public void defineExport(String name, Value value) {
		throw new IllegalStateException("Can't define new symbol to factory module");
	}

	@Override
	public ModuleContext load(MangoScriptModule source, ModuleResolver resolver) {
		throw new IllegalStateException("Can't load from source to factory module");
	}

	@Override
	public Map<String, Value> getExports() { return super.getConstants(); }
}
