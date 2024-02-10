package xyz.mangostudio.mangoscript.runtime.execution;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.runtime.factory.ObjectClassRuntimeType;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.TypeValue;
import xyz.mangostudio.mangoscript.runtime.value.number.I32Value;

class ExecutionContextTest {
	@Test
	void testScoped() {
		ModuleContext module = ModuleContext.create();
		ExecutionContext parent = module.newExecution();
		parent.defineLocal("a", new I32Value(1));
		assertEquals(new TypeValue(ObjectClassRuntimeType.CLASS), parent.getLocal("object"));
		assertEquals(new I32Value(1), parent.getLocal("a"));

		ExecutionContext scoped = parent.newScope();
		assertEquals(new I32Value(1), scoped.getLocal("a"));
		scoped.setLocal("a", new I32Value(2));
		assertEquals(new I32Value(2), scoped.getLocal("a"));
		assertEquals(new I32Value(2), parent.getLocal("a"));

		scoped.defineLocal("object", new I32Value(3));
		assertEquals(new I32Value(3), scoped.getLocal("object"));
		assertEquals(new TypeValue(ObjectClassRuntimeType.CLASS), parent.getLocal("object"));
	}
}
