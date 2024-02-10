package xyz.mangostudio.mangoscript.runtime.module;

import java.io.Serial;

public class ModuleException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 804259835363144668L;

	public ModuleException(String message) {
		super(message);
	}
}
