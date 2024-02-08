package xyz.mangostudio.mangoscript.binary.stmt;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.type.Type;

public record DefineVariableStatement(Type type, String name, Expression expression) implements Statement {
	public DefineVariableStatement(Type type, String name) {
		this(type, name, null);
	}

	@Override
	public String toString() {
		if (expression == null) return type + " " + name + ";";
		return type + " " + name + " = " + expression + ";";
	}
}
