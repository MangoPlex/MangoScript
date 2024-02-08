package xyz.mangostudio.mangoscript.binary.module;

import java.io.Serial;

public class ModuleException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -4312751012185117384L;

	public ModuleException(String message) {
		super(message);
	}
}
