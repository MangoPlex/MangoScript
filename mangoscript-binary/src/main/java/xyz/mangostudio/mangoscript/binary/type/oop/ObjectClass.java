package xyz.mangostudio.mangoscript.binary.type.oop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.mangostudio.mangoscript.binary.func.Function;
import xyz.mangostudio.mangoscript.binary.func.FunctionSignature;
import xyz.mangostudio.mangoscript.binary.func.GuestFunction;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.binary.type.Type;

public class ObjectClass {
	private String name;
	private ClassType superclass;
	private Map<String, Type> fieldsMap = new HashMap<>();
	private List<Map.Entry<String, Type>> fields = new ArrayList<>();
	private Map<String, Function> functionsMap = new HashMap<>();

	public ObjectClass(String name, ClassType superclass) {
		this.name = name;
		this.superclass = superclass;
	}

	public ObjectClass(String name) {
		this.name = name;
		this.superclass = new ClassType("object");
	}

	public String getName() { return name; }

	/**
	 * <p>
	 * Get the reference to superclass of this class. Because this
	 * {@link ObjectClass} is a binary representation, there is no way to figure out
	 * if this class is a subclass of a specific class.
	 * </p>
	 * 
	 * @return The class reference.
	 */
	public ClassType getSuperclass() { return superclass; }

	/**
	 * <p>
	 * Get a map of fields, with key as field name and value as field type. Fields
	 * in this class can override the fields in parent class (and maybe with a
	 * different type, but during validation, the type must be a subclass of
	 * superclass's field type).
	 * </p>
	 * 
	 * @return Map of fields.
	 */
	public Map<String, Type> getFieldsMap() { return Collections.unmodifiableMap(fieldsMap); }

	/**
	 * <p>
	 * Get a list of fields in defined order (first defined field at index 0, second
	 * defined field at 1 and so on...).
	 * </p>
	 * <p>
	 * For example, in the code {@code f32 x; f32 y;}, the first field from this
	 * list is {@code x} and the second field is {@code y}.
	 * </p>
	 * <p>
	 * If you want to lookup from field name, use {@link #getFieldsMap()}. This
	 * method is only used for generating default constructor.
	 * </p>
	 * 
	 * @return A list of fields in defined order.
	 */
	public List<Map.Entry<String, Type>> getFields() { return Collections.unmodifiableList(fields); }

	public Map<String, Function> getFunctionsMap() { return Collections.unmodifiableMap(functionsMap); }

	public ObjectClass defineField(String name, Type type) {
		if (fieldsMap.containsKey(name)) throw new RuntimeException("'" + name + "' field is already defined");
		fieldsMap.put(name, type);
		fields.add(Map.entry(name, type));
		return this;
	}

	public ObjectClass defineFunction(String name, FunctionSignature signature, String[] arguments, Statement code) {
		return defineFunction(name, new GuestFunction(name, signature, arguments, code));
	}

	public ObjectClass defineFunction(String name, Function function) {
		if (functionsMap.containsKey(name)) throw new RuntimeException("'" + name + "' function is already defined");
		functionsMap.put(name, function);
		return this;
	}

	public ClassType toRef() {
		return new ClassType(name);
	}

	@Override
	public String toString() {
		return name != null ? name : "[anonymous class]";
	}
}
