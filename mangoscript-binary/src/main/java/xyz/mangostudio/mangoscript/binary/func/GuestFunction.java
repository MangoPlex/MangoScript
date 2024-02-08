package xyz.mangostudio.mangoscript.binary.func;

import java.util.HashMap;
import java.util.Map;

import xyz.mangostudio.mangoscript.binary.stmt.Statement;

public class GuestFunction implements Function {
	private String name;
	private FunctionSignature signature;
	private Map<String, Integer> argumentsMapping = new HashMap<>();
	private String[] argNames;
	private Statement code;

	public GuestFunction(String name, FunctionSignature signature, String[] argNames, Statement code) {
		this.name = name;
		this.signature = signature;
		this.argNames = argNames;
		this.code = code;
	}

	@Override
	public FunctionSignature getFunctionSignature() { return signature; }

	public Statement getCode() { return code; }

	public Map<String, Integer> getArgumentsMapping() { return argumentsMapping; }

	public String getName() { return name; }

	@Override
	public String toString() {
		String prefix = signature.output().toString() + (name != null ? " " + name + "(" : "(");
		String args = "";

		for (int i = 0; i < signature.parameters().length; i++) {
			args += args.isEmpty() ? "" : ", ";
			args += signature.parameters()[i].toString() + " " + argNames[i];
		}

		return prefix + args + ") " + code;
	}
}
