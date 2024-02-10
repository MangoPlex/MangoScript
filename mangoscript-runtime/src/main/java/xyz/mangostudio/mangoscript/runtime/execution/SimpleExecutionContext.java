package xyz.mangostudio.mangoscript.runtime.execution;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public class SimpleExecutionContext implements ExecutionContext {
	private ModuleContext module;
	private ExecutionContext parent;
	private Value thisValue;
	private Map<String, Value> locals = new HashMap<>();

	public SimpleExecutionContext(ModuleContext module, ExecutionContext parent, Value thisValue) {
		this.module = module;
		this.parent = parent;
		this.thisValue = thisValue;
	}

	public SimpleExecutionContext(ModuleContext module, Value thisValue) {
		this(module, null, thisValue);
	}

	public SimpleExecutionContext(ModuleContext module) {
		this(module, null, null);
	}

	@Override
	public ModuleContext getModule() { return module; }

	@Override
	public Value getThis() { return thisValue; }

	@Override
	public Value getLocal(String name) {
		Value v = locals.get(name);
		if (v == null && parent != null) v = parent.getLocal(name);
		if (v == null) v = module.getConstants().get(name);
		if (v == null) throw new RuntimeException("'" + name + "' is not defined");
		return v;
	}

	@Override
	public boolean isLocalDefined(String name) {
		return isLocalInThisScope(name)
			|| (parent != null && parent.isLocalDefined(name))
			|| module.getConstants().containsKey(name);
	}

	@Override
	public boolean isLocalInThisScope(String name) {
		return locals.containsKey(name);
	}

	@Override
	public void setLocal(String name, Value value) {
		if (isLocalInThisScope(name)) {
			locals.put(name, value);
			return;
		}

		if (parent != null && parent.isLocalInThisScope(name)) {
			parent.setLocal(name, value);
			return;
		}

		throw new RuntimeException("'" + name + "' is not defined");
	}

	@Override
	public void defineLocal(String name, Value value) {
		if (isLocalInThisScope(name) || (parent != null && parent.isLocalInThisScope(name)))
			throw new RuntimeException("'" + name + "' is already defined");
		locals.put(name, value);
	}

	@Override
	public String toString() {
		return "context {\n"
			+ "    " + module + "\n"
			+ (parent != null
				? "    parent {\n"
					+ Stream.of(parent.toString().split("\n")).map(v -> "    " + v).collect(Collectors.joining("\n"))
					+ "\n}"
				: "")
			+ locals.entrySet().stream().map(v -> "    " + v.getKey() + " = " + v.getValue() + "\n")
				.collect(Collectors.joining(""))
			+ "}";
	}
}
