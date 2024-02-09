package xyz.mangostudio.mangoscript.binary.type;

public enum PrimitiveType implements Type {
	I8,
	I16,
	I32,
	I64,
	F32,
	F64,
	ANY,
	VOID,
	TYPE;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}
}
