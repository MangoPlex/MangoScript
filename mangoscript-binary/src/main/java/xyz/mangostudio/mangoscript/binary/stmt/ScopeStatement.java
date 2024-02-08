package xyz.mangostudio.mangoscript.binary.stmt;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public record ScopeStatement(Statement[] children) implements Statement {
	@Override
	public String toString() {
		String content = Stream.of(children)
			.flatMap(child -> Stream.of(child.toString().split("\n")))
			.map(s -> "    " + s)
			.collect(Collectors.joining("\n"));
		return "{\n" + content + "\n}";
	}
}
