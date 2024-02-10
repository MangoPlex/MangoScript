package xyz.mangostudio.mangoscript.runtime.execution;

import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.runtime.value.Value;

public interface ExecutionContext {
	public ModuleContext getModule();

	public Value getThis();

	public Value getLocal(String name);

	public void setLocal(String name, Value value);

	public boolean isLocalDefined(String name);

	public boolean isLocalInThisScope(String name);

	public void defineLocal(String name, Value value);

	default ExecutionContext newScope() {
		return new SimpleExecutionContext(getModule(), this, getThis());
	}
}
