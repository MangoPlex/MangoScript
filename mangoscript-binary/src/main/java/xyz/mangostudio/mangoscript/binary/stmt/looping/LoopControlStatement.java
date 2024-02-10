package xyz.mangostudio.mangoscript.binary.stmt.looping;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;

public enum LoopControlStatement implements Statement {
	BREAK,
	CONTINUE;

	@Override
	public String toString() {
		return super.toString().toLowerCase() + ";";
	}
}
