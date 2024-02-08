package xyz.mangostudio.mangoscript.binary.type;

public interface Type {
	default ArrayType arrayVariant() {
		return new ArrayType(this);
	}

	default OptionalType optionalVariant() {
		return new OptionalType(this);
	}
}
