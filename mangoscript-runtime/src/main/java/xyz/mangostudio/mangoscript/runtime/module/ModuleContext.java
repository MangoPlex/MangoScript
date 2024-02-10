package xyz.mangostudio.mangoscript.runtime.module;

import java.util.Map;
import java.util.Map.Entry;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.module.MangoScriptModule;
import xyz.mangostudio.mangoscript.binary.type.ArrayType;
import xyz.mangostudio.mangoscript.binary.type.OptionalType;
import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.binary.type.oop.ClassType;
import xyz.mangostudio.mangoscript.binary.type.oop.ObjectClass;
import xyz.mangostudio.mangoscript.runtime.execution.ExecutionContext;
import xyz.mangostudio.mangoscript.runtime.execution.SimpleExecutionContext;
import xyz.mangostudio.mangoscript.runtime.factory.F32ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.factory.F64ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.factory.FactoryModuleContext;
import xyz.mangostudio.mangoscript.runtime.factory.I16ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.factory.I32ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.factory.I64ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.factory.I8ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.type.GuestClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.TypeValue;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public interface ModuleContext {
	public Map<String, ClassRuntimeType> getClasses();

	public Map<String, Function> getFunctions();

	public Map<String, Value> getConstants();

	public Map<String, Value> getExports();

	public void defineClass(String name, ClassRuntimeType objectClass);

	public void defineFunction(String name, Function function);

	public void defineConstant(String name, Value value);

	public void defineExport(String name, Value value);

	default boolean canDefine(String name) {
		return !getConstants().containsKey(name);
	}

	default void ensureCanDefine(String name) {
		if (!canDefine(name)) throw new ModuleException("'" + name + "' is already defined");
	}

	default Type resolveType(Type source) {
		if (source instanceof ClassType ct) {
			ClassRuntimeType rt = getClasses().get(ct.type());
			if (rt == null) throw new RuntimeException("'" + ct.type() + "' is not a class");
			return rt;
		}

		if (source instanceof OptionalType wrapper) return new OptionalType(resolveType(wrapper.type()));
		if (source instanceof ArrayType wrapper) return new ArrayType(resolveType(wrapper.base()));
		return source;
	}

	default FunctionSignature resolveSignature(FunctionSignature signature) {
		Type output = resolveType(signature.output());
		Type[] params = new Type[signature.parameters().length];
		for (int i = 0; i < params.length; i++) params[i] = resolveType(signature.parameters()[i]);
		return new FunctionSignature(output, params);
	}

	default ClassRuntimeType typeToClass(Type source) {
		source = resolveType(source);
		if (source instanceof ClassRuntimeType rt) return rt;
		if (source instanceof PrimitiveType pt) return switch (pt) {
		case I8 -> I8ClassRuntimeType.CLASS;
		case I16 -> I16ClassRuntimeType.CLASS;
		case I32 -> I32ClassRuntimeType.CLASS;
		case I64 -> I64ClassRuntimeType.CLASS;
		case F32 -> F32ClassRuntimeType.CLASS;
		case F64 -> F64ClassRuntimeType.CLASS;
		default -> throw new RuntimeException("Can't get class from " + pt + " primitive");
		};

		throw new RuntimeException("Can't get class from " + source);
	}

	default ModuleContext importFrom(ModuleContext module) {
		for (Entry<String, Value> e : module.getExports().entrySet()) {
			String name = e.getKey();
			Value value = e.getValue();
			ensureCanDefine(name);

			if (value instanceof TypeValue tv && tv.targetType() instanceof ClassRuntimeType rt)
				defineClass(name, rt);

			defineConstant(name, value);
		}

		return this;
	}

	default ModuleContext load(MangoScriptModule source, ModuleResolver resolver) {
		for (String importPath : source.getImports()) {
			ModuleContext module = resolver.resolve(this, importPath);
			if (module == null) throw new ModuleException("Module not found: " + importPath);
			// TODO import
			throw new RuntimeException("Not yet implemented");
		}

		for (ObjectClass classSource : source.getClasses()) {
			String className = classSource.getName();
			ensureCanDefine(className);
			defineClass(className, new GuestClassRuntimeType(this, classSource));
		}

		for (Entry<String, Function> e : source.getFunctions().entrySet()) {
			String funcName = e.getKey();
			Function function = e.getValue();
			ensureCanDefine(funcName);
			defineFunction(funcName, function);
		}

		for (Entry<String, String> e : source.getExports().entrySet()) {
			String exportAs = e.getKey();
			String target = e.getValue();
			Value c = getConstants().get(target);
			if (c == null)
				throw new ModuleException("Can't export '" + exportAs + "' because '" + c + "' is not defined");
			defineExport(exportAs, c);
		}

		return this;
	}

	default ExecutionContext newExecutionOf(Value thisValue) {
		return new SimpleExecutionContext(this, thisValue);
	}

	default ExecutionContext newExecution() {
		return newExecutionOf(null);
	}

	public static ModuleContext create() {
		return new SimpleModuleContext().importFrom(FactoryModuleContext.FACTORY);
	}
}
