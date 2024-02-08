package xyz.mangostudio.mangoscript.binary.type.oop;

import xyz.mangostudio.mangoscript.binary.type.Type;

/**
 * <p>
 * Class type refers to a class in which the module can see it (either imported
 * or created in module context).
 * </p>
 * <p>
 * This is a reference to a class that visible in current module, not a class
 * itself, so in order to get the actual class, you need to rely on runtime.
 * </p>
 */
public record ClassType(String type) implements Type {
	@Override
	public String toString() {
		return type;
	}
}
