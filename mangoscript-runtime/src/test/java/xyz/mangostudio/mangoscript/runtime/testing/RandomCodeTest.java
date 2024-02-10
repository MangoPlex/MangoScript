package xyz.mangostudio.mangoscript.runtime.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F32Value;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;

class RandomCodeTest {
	@Test
	void testFibFunction() {
		ModuleContext module = TestUtils.parseModule("""
			i32 fib(i32 index) {
				i32 a = 0;
				i32 b = 1;

				for (i32 i = 0; i < index; i++) {
					i32 c = a + b;
					a = b;
					b = c;
				}

				return b;
			}
			""");
		assertEquals(fib(10), module.getConstants().get("fib").call(new I32Value(10)));
	}

	I32Value fib(int index) {
		int a = 0;
		int b = 1;

		for (int i = 0; i < index; i++) {
			int c = a + b;
			a = b;
			b = c;
		}

		return new I32Value(b);
	}

	@Test
	void testRecursive() {
		ModuleContext module = TestUtils.parseModule("""
			i32 recursive(i32 depth) {
				if (depth > 0) return recursive(depth - 1);
				else return 0;
			}
			""");
		assertEquals(new I32Value(0), module.getConstants().get("recursive").call(new I32Value(10)));
	}

	@Test
	void testMultipleFunctions() {
		ModuleContext module = TestUtils.parseModule("""
			i32 indirectCall() {
				return recursive(10);
			}

			i32 recursive(i32 depth) {
				if (depth > 0) return recursive(depth - 1);
				else return 0;
			}
			""");
		assertEquals(new I32Value(0), module.getConstants().get("indirectCall").call());
	}

	@Test
	void testOperatorOverloading() {
		ModuleContext module = TestUtils.parseModule("""
			class vec2 {
				f32 x;
				f32 y;

				vec2 +(vec2 another) return vec2(this.x + another.x, this.y + another.y);
			}
			""");

		Value a = module.getConstants().get("vec2").call(new F32Value(5), new F32Value(10));
		Value b = module.getConstants().get("vec2").call(new F32Value(7), new F32Value(9));
		assertEquals(new F32Value(5 + 7), a.add(b).getProperty("x"));
		assertEquals(new F32Value(10 + 9), a.add(b).getProperty("y"));
	}
}
