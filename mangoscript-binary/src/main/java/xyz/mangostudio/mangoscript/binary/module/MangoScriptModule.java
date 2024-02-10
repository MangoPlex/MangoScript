package xyz.mangostudio.mangoscript.binary.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.binary.type.oop.ObjectClass;

public class MangoScriptModule {
	private List<String> imports = new ArrayList<>();
	private Set<String> definedSymbols = new HashSet<>();
	private Map<String, String> exports = new HashMap<>();
	private List<ObjectClass> classes = new ArrayList<>();
	private Map<String, Function> functions = new HashMap<>();
	// TODO private Map<String, Expression> constants = new HashMap<>();

	public List<String> getImports() { return Collections.unmodifiableList(imports); }

	/**
	 * <p>
	 * Get a map of exports, with key as "exported as name" and value as name of
	 * symbol to export.
	 * </p>
	 * 
	 * @return Exports map.
	 */
	public Map<String, String> getExports() { return Collections.unmodifiableMap(exports); }

	public List<ObjectClass> getClasses() { return Collections.unmodifiableList(classes); }

	public ObjectClass getClass(String name) {
		for (ObjectClass clazz : getClasses()) if (name.equals(clazz.getName())) return clazz;
		return null;
	}

	public Map<String, Function> getFunctions() { return Collections.unmodifiableMap(functions); }

	public Set<String> getDefinedSymbols() { return Collections.unmodifiableSet(definedSymbols); }

	public MangoScriptModule withImport(String path) {
		imports.add(path);
		return this;
	}

	public MangoScriptModule withExport(String target, String exportAs) {
		if (exports.containsKey(exportAs))
			throw new ModuleException("Cannot export '" + target + "' as '" + exportAs + "' because '" + exportAs
				+ "' is already exported");
		exports.put(exportAs, target);
		return this;
	}

	public MangoScriptModule withClass(String name, ObjectClass clazz) {
		if (definedSymbols.contains(name)) throw new ModuleException("'" + name + "' is already defined");
		classes.add(clazz);
		definedSymbols.add(name);
		return this;
	}

	public MangoScriptModule withFunction(String name, Function function) {
		if (definedSymbols.contains(name)) throw new ModuleException("'" + name + "' is already defined");
		functions.put(name, function);
		definedSymbols.add(name);
		return this;
	}

	@Override
	public String toString() {
		String content = ""
			+ imports.stream().map(v -> "    import \"" + v + "\";\n").collect(Collectors.joining(""))
			+ classes.stream().map(v -> "    class " + v + ";\n").collect(Collectors.joining(""))
			+ functions.entrySet().stream()
				.flatMap(e -> Stream.of(e.getValue().toString().split("\n")))
				.map(e -> "    " + e + "\n")
				.collect(Collectors.joining(""))
			+ exports.entrySet().stream().map(v -> "    export " + v.getValue() + " as " + v.getKey() + ";\n")
				.collect(Collectors.joining(""));
		return "module {\n" + content + "}";
	}
}
