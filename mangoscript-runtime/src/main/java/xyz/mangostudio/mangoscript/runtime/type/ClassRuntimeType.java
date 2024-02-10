package xyz.mangostudio.mangoscript.runtime.type;

import java.util.List;
import java.util.Map;

import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.binary.type.oop.ClassType;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.object.ObjectValue;

/**
 * <p>
 * Unlike {@link ClassType}, {@link ClassRuntimeType} is a runtime
 * representation of the class (or type). This runtime type contains actual
 * superclass, which is another {@link ClassRuntimeType}.
 * </p>
 */
public interface ClassRuntimeType extends Type {
	/**
	 * <p>
	 * Get the module context that is owning this class. Functions called in this
	 * class can only interact with this module and the object.
	 * </p>
	 * 
	 * @return The module context.
	 */
	public ModuleContext getModule();

	/**
	 * <p>
	 * Get the superclass of this class.
	 * </p>
	 * 
	 * @return The superclass in runtime type.
	 */
	public ClassRuntimeType getSuperclass();

	default Value getPropertyOf(Value target, String name) {
		if (target instanceof ObjectValue ov && ov.getType().isSubclassOf(this)) {
			return getSuperclass().getPropertyOf(target, name);
		}

		return null;
	}

	/**
	 * <p>
	 * Get all fields of this class, including fields from superclasses. The fields
	 * are sorted by defined order.
	 * </p>
	 * 
	 * @return A list of fields.
	 */
	default List<Map.Entry<String, Type>> getAllFields() { return getSuperclass().getAllFields(); }

	default boolean isSubclassOf(ClassRuntimeType target) {
		if (this == target) return true;
		if (getSuperclass() == target) return true;
		if (this == getSuperclass()) return false;
		return getSuperclass().isSubclassOf(target);
	}

	default Value createDefault() {
		return new ObjectValue(this);
	}

	/**
	 * <p>
	 * Call the constructor of this class.
	 * </p>
	 * 
	 * @param args Constructor arguments.
	 * @return A constructed value.
	 */
	default Value create(Value... args) {
		return createDefault();
	}
}
