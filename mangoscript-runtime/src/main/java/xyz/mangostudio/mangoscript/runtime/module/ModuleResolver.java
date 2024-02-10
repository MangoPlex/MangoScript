package xyz.mangostudio.mangoscript.runtime.module;

@FunctionalInterface
public interface ModuleResolver {
	/**
	 * <p>
	 * Attempt to resolve module with given source and path.
	 * </p>
	 * 
	 * @param requestedFrom The origin module that requested import.
	 * @param path          The path to module to resolve and import.
	 * @return The resolved module context, ready for importing.
	 */
	public ModuleContext resolve(ModuleContext requestedFrom, String path);
}
