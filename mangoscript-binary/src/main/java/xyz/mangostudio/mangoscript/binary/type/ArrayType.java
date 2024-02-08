package xyz.mangostudio.mangoscript.binary.type;

public record ArrayType(Type base) implements Type {
	public static ArrayType depth(Type base, int depth) {
		if (depth < 1) throw new IllegalArgumentException("Depth can't be less than 1");
		ArrayType arr = new ArrayType(base);
		depth--;

		while (depth > 0) {
			arr = new ArrayType(arr);
			depth--;
		}

		return arr;
	}

	@Override
	public String toString() {
		return base + "[]";
	}
}
