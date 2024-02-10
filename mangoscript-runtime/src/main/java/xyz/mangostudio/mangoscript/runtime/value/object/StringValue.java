package xyz.mangostudio.mangoscript.runtime.value.object;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.binary.type.Type;
import xyz.mangostudio.mangoscript.runtime.factory.StringClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.type.ClassOfPrimitive;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F64Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I16Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I64Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I8Value;

public class StringValue extends ObjectValue {
	private String content;

	public StringValue(String content) {
		super(StringClassRuntimeType.CLASS);
		if (content == null) throw new NullPointerException("content can't be null (consider use string? instead)");
		this.content = content;
	}

	public StringValue() {
		this("");
	}

	public String getContent() { return content; }

	@Override
	public boolean equals(Object obj) {
		return obj instanceof StringValue v && content.equals(v.content);
	}

	@Override
	public Value castTo(Type targetType) {
		if (targetType instanceof ClassOfPrimitive cop) targetType = cop.getPrimitive();
		if (targetType == PrimitiveType.I8) return new I8Value(Byte.parseByte(content));
		if (targetType == PrimitiveType.I16) return new I16Value(Short.parseShort(content));
		if (targetType == PrimitiveType.I32) return new I32Value(Integer.parseInt(content));
		if (targetType == PrimitiveType.I64) return new I64Value(Long.parseLong(content));
		if (targetType == PrimitiveType.F32) return new F32Value(Float.parseFloat(content));
		if (targetType == PrimitiveType.F64) return new F64Value(Double.parseDouble(content));
		return super.castTo(targetType);
	}

	@Override
	public Value add(Value another) {
		if (another instanceof StringValue s) return new StringValue(content + s.content);
		return new StringValue(content + another);
	}

	@Override
	public String toString() {
		return '"' + content + '"';
	}
}
