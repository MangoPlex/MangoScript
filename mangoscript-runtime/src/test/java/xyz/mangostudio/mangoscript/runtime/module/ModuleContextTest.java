package xyz.mangostudio.mangoscript.runtime.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.binary.module.MangoScriptModule;
import xyz.mangostudio.mangoscript.runtime.factory.ObjectClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.value.Value;
import xyz.mangostudio.mangoscript.runtime.value.number.F32Value;
import xyz.mangostudio.mangoscript.text.parser.ModuleParser;

class ModuleContextTest {
	@Test
	void testClass() {
		String code = """
			class vec2 {
				f32 x;
				f32 y;
			}

			class vec3 extends vec2 {
				f32 z;
			}
			""";
		MangoScriptModule module = ModuleParser.parse(new StringReader(code));
		ModuleContext context = ModuleContext.create().load(module, null);
		assertTrue(context.getClasses().get("vec2").isSubclassOf(ObjectClassRuntimeType.CLASS));
		assertTrue(context.getClasses().get("vec3").isSubclassOf(ObjectClassRuntimeType.CLASS));
		assertTrue(context.getClasses().get("vec3").isSubclassOf(context.getClasses().get("vec2")));

		Value vec2 = Value.createDefault(context.getClasses().get("vec2"));
		assertEquals(new F32Value(0), vec2.getProperty("x"));
		assertEquals(new F32Value(0), vec2.getProperty("y"));

		Value vec3 = Value.createDefault(context.getClasses().get("vec3"));
		assertEquals(new F32Value(0), vec3.getProperty("x"));
		assertEquals(new F32Value(0), vec3.getProperty("y"));
		assertEquals(new F32Value(0), vec3.getProperty("z"));
	}
}
