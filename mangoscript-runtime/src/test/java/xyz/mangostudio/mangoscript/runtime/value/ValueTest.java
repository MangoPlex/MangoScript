package xyz.mangostudio.mangoscript.runtime.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;

class ValueTest {
	@Test
	void testDefaults() {
		assertEquals(new I32Value(0), Value.createDefault(PrimitiveType.I32));
		assertEquals(new OptionalValue(PrimitiveType.I32.optionalVariant()),
			Value.createDefault(PrimitiveType.I32.optionalVariant()));
	}
}
