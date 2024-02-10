package xyz.mangostudio.mangoscript.runtime.value.object;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.type.ClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.type.GuestClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public class ObjectValue implements Value {
	private ClassRuntimeType type;
	private Map<String, Value> fields = new HashMap<>();

	public ObjectValue(ClassRuntimeType type) {
		this.type = type;
		fillDefaultsToFields(type);
	}

	private void fillDefaultsToFields(ClassRuntimeType type) {
		for (Entry<String, Type> e : type.getAllFields()) fields.put(e.getKey(), Value.createDefault(e.getValue()));
	}

	@Override
	public ClassRuntimeType getType() { return type; }

	public Map<String, Value> getFields() { return fields; }

	@Override
	public Value getProperty(String name) {
		Value v = fields.get(name);
		if (v == null) v = type.getPropertyOf(this, name);
		if (v == null) v = Value.super.getProperty(name);
		return v;
	}

	@Override
	public void setProperty(String name, Value value) {
		if (fields.containsKey(name)) fields.put(name, value);
		else Value.super.setProperty(name, value);
	}

	@Override
	public Value castTo(Type targetType) {
		if (targetType instanceof ClassRuntimeType rt && type.isSubclassOf(rt)) return this;
		return Value.super.castTo(targetType);
	}

	@Override
	public String toString() {
		String name = type instanceof GuestClassRuntimeType guest ? guest.getSource().toString() : type.toString();
		return name + "("
			+ fields.entrySet().stream().map(v -> v.getKey() + ": " + v.getValue()).collect(Collectors.joining(", "))
			+ ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof ObjectValue another)) return false;
		if (type == another.type) return false;
		return fields.equals(another.fields);
	}
}
