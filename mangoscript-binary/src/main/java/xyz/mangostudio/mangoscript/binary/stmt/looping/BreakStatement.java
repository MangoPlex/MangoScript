package xyz.mangostudio.mangoscript.binary.stmt.looping;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;

public record BreakStatement(String label) implements Statement {
	@Override
	public String toString() {
		return label != null ? "break " + label + ";" : "break;";
	}
}
