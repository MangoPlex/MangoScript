package xyz.mangostudio.mangoscript.text.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.binary.module.MangoScriptModule;

class ModuleParserTest {
	@Test
	void test() {
		String code = """
			import "path/to/module.ms";

			class vec2 {
				f32 x;
				f32 y;

				vec2 +(vec2 another) return vec2(this.x + another.x, this.y + another.y);
				void classFunction() {}

				i32 sus(i32 x, i32 y) {
					this.x = x;
					this.y = y;
				}
			}

			void main(string[] args) {
				string();
				myFunction(1, 2, 3, 0xDEADBEEF);
				string sus;
				string text = "Hello " + "world" + "!";
				i32 v = 0 + 1b + 0x1y + 01b + 2s + 0x2s + 02s + 3 + 0x3 + 03 + 3i + 0x3i + 03i + 4l + 0x4l + 04l;
				f32 x = (0 * (0.0 * 1f)) + (1d + 0.f + 0.d) + 0.1f + 0.1d;
				1 + 2 * 3 / 4 - 5 > 6 < 7;

				if (x > 1f) {
					print("SIKE");
				} else {
					print("not sike :(");
				}

				// Unary
				v++;
				v--;
				-v;
				!v;
				++v;
				--v;
				-1;

				while (true) break;
				label0001: while (true) break label0001;
				do break; while (true);
				label0002: do break label0002; while (true);
				for (;;) break;
				for (i32 i = 0; i < 10; i++) {
					break;
				}
				for (vec2 v = vec2(0, 10); v.x < v.y; v.x++) print(v);
				label003: for(;;) break label003;

				print(0x7F | 0x80 == 0b11111111);
			}

			class vec3 extends vec2 {
				f32 z;
			}

			void castTest(vec2 v) {
				v.castTo(vec3).z = v.x;
				v.x = v.y;
			}

			export vec2;
			export vec3;
			export main as entryPoint;
			export castTest;
			""";

		MangoScriptModule module = ModuleParser.parse(new StringReader(code));
		assertEquals("path/to/module.ms", module.getImports().get(0));
		assertNotNull(module.getFunctions().get("main"));
		assertNotNull(module.getFunctions().get("castTest"));
		assertNotNull(module.getClasses().get("vec2"));
		assertNotNull(module.getClasses().get("vec3"));
		assertEquals("vec2", module.getExports().get("vec2"));
		assertEquals("vec3", module.getExports().get("vec3"));
		assertEquals("main", module.getExports().get("entryPoint"));
		assertEquals("castTest", module.getExports().get("castTest"));

		System.out.println(module);
	}
}
