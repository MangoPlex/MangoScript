package xyz.mangostudio.mangoscript.binary.type;

public record OptionalType(Type type) implements Type {
	@Override
	public String toString() {
		return type + "?";
	}
}
