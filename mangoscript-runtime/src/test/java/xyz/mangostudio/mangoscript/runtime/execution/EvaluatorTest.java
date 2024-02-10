package xyz.mangostudio.mangoscript.runtime.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static xyz.mangostudio.mangoscript.runtime.testing.TestUtils.parseExp;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.binary.type.PrimitiveType;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.ArrayValue;
import xyz.mangostudio.mangoscript.runtime.value.OptionalValue;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F64Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I16Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I64Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I8Value;
import xyz.mangostudio.mangoscript.runtime.value.object.StringValue;

class EvaluatorTest {
	@Test
	void testEvalString() {
		ModuleContext module = ModuleContext.create();
		assertEquals(
			new StringValue("Hello world!"),
			Evaluator.evaluate(module.newExecution(), parseExp("\"Hello world!\"")));
		assertEquals(new I32Value(12), Evaluator.evaluate(module.newExecution(), parseExp("\"Hello world!\".length")));
		assertEquals(new StringValue("Hello 42"),
			Evaluator.evaluate(module.newExecution(), parseExp("\"Hello \" + 42")));
		assertEquals(new StringValue("42 Hello"),
			Evaluator.evaluate(module.newExecution(), parseExp("42 + \" Hello\"")));
	}

	@Test
	void testEvalI32I32() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new I32Value(123 + 456), Evaluator.evaluate(module.newExecution(), parseExp("123 + 456")));
		assertEquals(new I32Value(123 - 456), Evaluator.evaluate(module.newExecution(), parseExp("123 - 456")));
		assertEquals(new I32Value(123 * 456), Evaluator.evaluate(module.newExecution(), parseExp("123 * 456")));
		assertEquals(new I32Value(123 / 456), Evaluator.evaluate(module.newExecution(), parseExp("123 / 456")));
		assertEquals(new I32Value(123 & 456), Evaluator.evaluate(module.newExecution(), parseExp("123 & 456")));
		assertEquals(new I32Value(123 | 456), Evaluator.evaluate(module.newExecution(), parseExp("123 | 456")));
		assertEquals(new I32Value(123 ^ 456), Evaluator.evaluate(module.newExecution(), parseExp("123 ^ 456")));
		assertEquals(new I32Value(123 << 4), Evaluator.evaluate(module.newExecution(), parseExp("123 << 4")));
		assertEquals(new I32Value(123 >> 4), Evaluator.evaluate(module.newExecution(), parseExp("123 >> 4")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123 > 456")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123 < 456")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123 >= 456")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123 <= 456")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123 == 456")));
	}

	@Test
	void testEvalI64I64() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new I64Value(123 + 456), Evaluator.evaluate(module.newExecution(), parseExp("123l + 456l")));
		assertEquals(new I64Value(123 - 456), Evaluator.evaluate(module.newExecution(), parseExp("123l - 456l")));
		assertEquals(new I64Value(123 * 456), Evaluator.evaluate(module.newExecution(), parseExp("123l * 456l")));
		assertEquals(new I64Value(123 / 456), Evaluator.evaluate(module.newExecution(), parseExp("123l / 456l")));
		assertEquals(new I64Value(123 & 456), Evaluator.evaluate(module.newExecution(), parseExp("123l & 456l")));
		assertEquals(new I64Value(123 | 456), Evaluator.evaluate(module.newExecution(), parseExp("123l | 456l")));
		assertEquals(new I64Value(123 ^ 456), Evaluator.evaluate(module.newExecution(), parseExp("123l ^ 456l")));
		assertEquals(new I64Value(123 << 4), Evaluator.evaluate(module.newExecution(), parseExp("123l << 4l")));
		assertEquals(new I64Value(123 >> 4), Evaluator.evaluate(module.newExecution(), parseExp("123l >> 4l")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123l > 456l")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123l < 456l")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123l >= 456l")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123l <= 456l")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123l == 456l")));
	}

	@Test
	void testEvalF32F32() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new F32Value(123f + 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f + 456f")));
		assertEquals(new F32Value(123f - 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f - 456f")));
		assertEquals(new F32Value(123f * 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f * 456f")));
		assertEquals(new F32Value(123f / 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f / 456f")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123f > 456f")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123f < 456f")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123f >= 456f")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123f <= 456f")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123f == 456f")));
	}

	@Test
	void testEvalF64F64() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new F64Value(123d + 456d), Evaluator.evaluate(module.newExecution(), parseExp("123d + 456d")));
		assertEquals(new F64Value(123d - 456d), Evaluator.evaluate(module.newExecution(), parseExp("123d - 456d")));
		assertEquals(new F64Value(123d * 456d), Evaluator.evaluate(module.newExecution(), parseExp("123d * 456d")));
		assertEquals(new F64Value(123d / 456d), Evaluator.evaluate(module.newExecution(), parseExp("123d / 456d")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123d > 456d")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123d < 456d")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123d >= 456d")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123d <= 456d")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123d == 456d")));
	}

	@Test
	void testEvalI32F32() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new F32Value(123f + 456f), Evaluator.evaluate(module.newExecution(), parseExp("123i + 456f")));
		assertEquals(new F32Value(123f - 456f), Evaluator.evaluate(module.newExecution(), parseExp("123i - 456f")));
		assertEquals(new F32Value(123f * 456f), Evaluator.evaluate(module.newExecution(), parseExp("123i * 456f")));
		assertEquals(new F32Value(123f / 456f), Evaluator.evaluate(module.newExecution(), parseExp("123i / 456f")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123i > 456f")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123i < 456f")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123i >= 456f")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123i <= 456f")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123i == 456f")));
	}

	@Test
	void testEvalF32I32() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new F32Value(123f + 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f + 456i")));
		assertEquals(new F32Value(123f - 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f - 456i")));
		assertEquals(new F32Value(123f * 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f * 456i")));
		assertEquals(new F32Value(123f / 456f), Evaluator.evaluate(module.newExecution(), parseExp("123f / 456i")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123f > 456i")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123f < 456i")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123f >= 456i")));
		assertEquals(I32Value.TRUE, Evaluator.evaluate(module.newExecution(), parseExp("123f <= 456i")));
		assertEquals(I32Value.FALSE, Evaluator.evaluate(module.newExecution(), parseExp("123f == 456i")));
	}

	@Test
	void testIdentities() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new I8Value((byte) 0), Evaluator.evaluate(module.newExecution(), parseExp("i8()")));
		assertEquals(new I16Value((short) 0), Evaluator.evaluate(module.newExecution(), parseExp("i16()")));
		assertEquals(new I32Value(0), Evaluator.evaluate(module.newExecution(), parseExp("i32()")));
		assertEquals(new I64Value(0), Evaluator.evaluate(module.newExecution(), parseExp("i64()")));
		assertEquals(new F32Value(0), Evaluator.evaluate(module.newExecution(), parseExp("f32()")));
		assertEquals(new F64Value(0), Evaluator.evaluate(module.newExecution(), parseExp("f64()")));
	}

	@Test
	void testCasting() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new I8Value((byte) 0), Evaluator.evaluate(module.newExecution(), parseExp("\"0\".castTo(i8)")));
		assertEquals(new I16Value((short) 0), Evaluator.evaluate(module.newExecution(), parseExp("\"0\".castTo(i16)")));
		assertEquals(new I32Value(0), Evaluator.evaluate(module.newExecution(), parseExp("\"0\".castTo(i32)")));
		assertEquals(new I64Value(0L), Evaluator.evaluate(module.newExecution(), parseExp("\"0\".castTo(i64)")));
		assertEquals(new F32Value(0f), Evaluator.evaluate(module.newExecution(), parseExp("\"0\".castTo(f32)")));
		assertEquals(new F64Value(0d), Evaluator.evaluate(module.newExecution(), parseExp("\"0\".castTo(f64)")));
	}

	@Test
	void testEvalOptional() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new OptionalValue(PrimitiveType.I32.optionalVariant()),
			Evaluator.evaluate(module.newExecution(), parseExp("i32?()")));
		assertEquals(new OptionalValue(PrimitiveType.I32.optionalVariant(), new I32Value(123)),
			Evaluator.evaluate(module.newExecution(), parseExp("i32?(123)")));
		assertEquals(new I32Value(1), Evaluator.evaluate(module.newExecution(), parseExp("i32?().isEmpty")));
		assertEquals(new I32Value(123), Evaluator.evaluate(module.newExecution(), parseExp("i32?(123).value")));
		assertEquals(new I32Value(456), Evaluator.evaluate(module.newExecution(), parseExp("i32?() ?? 456")));
		assertEquals(new I32Value(456), Evaluator.evaluate(module.newExecution(), parseExp("i32?() ?? 456 ?? i32?()")));
	}

	@Test
	void testEvalArray() {
		ModuleContext module = ModuleContext.create();
		assertEquals(new ArrayValue(PrimitiveType.I32.arrayVariant(), 0),
			Evaluator.evaluate(module.newExecution(), parseExp("i32[]()")));
		assertEquals(new ArrayValue(PrimitiveType.I32.arrayVariant(), 10),
			Evaluator.evaluate(module.newExecution(), parseExp("i32[](10)")));

		Value arr = Evaluator.evaluate(module.newExecution(), parseExp("i32[](10)"));
		arr.subscriptSet(new Value[] { new I32Value(0) }, new I32Value(123));
		assertEquals(new I32Value(123), arr.subscriptGet(new I32Value(0)));
	}
}
