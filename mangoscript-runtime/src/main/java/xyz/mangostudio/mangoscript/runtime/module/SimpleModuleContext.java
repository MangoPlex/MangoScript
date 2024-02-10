package xyz.mangostudio.mangoscript.runtime.module;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.TypeValue;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.hidden.StandaloneFunctionValue;

public class SimpleModuleContext implements ModuleContext {
	private Map<String, ClassRuntimeType> classes = new HashMap<>();
	private Map<String, Function> functions = new HashMap<>();
	private Map<String, Value> constants = new HashMap<>();
	private Map<String, Value> exports = new HashMap<>();

	@Override
	public Map<String, ClassRuntimeType> getClasses() { return Collections.unmodifiableMap(classes); }

	@Override
	public Map<String, Function> getFunctions() { return Collections.unmodifiableMap(functions); }

	@Override
	public Map<String, Value> getConstants() { return Collections.unmodifiableMap(constants); }

	@Override
	public Map<String, Value> getExports() { return Collections.unmodifiableMap(exports); }

	@Override
	public void defineClass(String name, ClassRuntimeType objectClass) {
		classes.put(name, objectClass);
		constants.put(name, new TypeValue(objectClass));
	}

	@Override
	public void defineFunction(String name, Function function) {
		functions.put(name, function);
		constants.put(name, new StandaloneFunctionValue(this, function));
	}

	@Override
	public void defineConstant(String name, Value value) {
		constants.put(name, value);
	}

	@Override
	public void defineExport(String name, Value value) {
		exports.put(name, value);
	}
}
