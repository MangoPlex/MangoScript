package xyz.mangostudio.mangoscript.binary.stmt.looping;

public record LabeledLoopingStatement(String name, LoopingStatement target) implements LoopingStatement {
	@Override
	public String toString() {
		return name + ": " + target;
	}
}
