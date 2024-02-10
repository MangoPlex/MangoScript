package xyz.mangostudio.mangoscript.runtime.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.binary.type.oop.ObjectClass;
import xyz.mangostudio.mangoscript.runtime.execution.AutoCast;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.hidden.TargetFunctionValue;
import xyz.mangostudio.mangoscript.runtime.value.object.ObjectValue;

public class GuestClassRuntimeType implements ClassRuntimeType {
	private ModuleContext module;
	private ObjectClass source;
	private ClassRuntimeType superclass;

	public GuestClassRuntimeType(ModuleContext module, ObjectClass source) {
		this.module = module;
		this.source = source;
		this.superclass = module.getClasses().get(source.getSuperclass().type());
		if (this.superclass == null)
			throw new RuntimeException("'" + source.getSuperclass().type() + "' is not a valid class");
	}

	public ObjectClass getSource() { return source; }

	@Override
	public ModuleContext getModule() { return module; }

	@Override
	public ClassRuntimeType getSuperclass() { return superclass; }

	@Override
	public List<Entry<String, Type>> getAllFields() {
		if (source.getFields().size() == 0) return ClassRuntimeType.super.getAllFields();

		List<Entry<String, Type>> fields = new ArrayList<>();
		fields.addAll(getSuperclass().getAllFields());
		for (Entry<String, Type> e : source.getFields())
			fields.add(Map.entry(e.getKey(), module.resolveType(e.getValue())));

		return fields;
	}

	@Override
	public Value getPropertyOf(Value target, String name) {
		Function function = source.getFunctionsMap().get(name);
		if (function != null) return new TargetFunctionValue(module, target, function);
		return ClassRuntimeType.super.getPropertyOf(target, name);
	}

	@Override
	public Value create(Value... args) {
		List<Entry<String, Type>> fields = getAllFields();
		if (fields.size() > args.length) throw new RuntimeException("Number of inputs exceeds constructor parameters: "
			+ fields.size() + " > " + args.length);

		ObjectValue out = (ObjectValue) createDefault();
		for (int i = 0; i < args.length; i++) {
			Entry<String, Type> entry = fields.get(i);
			out.getFields().put(entry.getKey(), AutoCast.to(entry.getValue(), args[i]));
		}

		return out;
	}

	@Override
	public String toString() {
		return "class " + source.getName() + " extends " + (superclass instanceof GuestClassRuntimeType gcrt
			? gcrt.source.getName()
			: superclass);
	}
}
