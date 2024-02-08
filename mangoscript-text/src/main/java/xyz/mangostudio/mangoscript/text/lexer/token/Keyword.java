package xyz.mangostudio.mangoscript.text.lexer.token;

/**
 * <p>
 * An enum of keywords. Some keywords are reserved for future use.
 * </p>
 */
public enum Keyword implements Token {
	IMPORT("import"),
	EXPORT("export"),
	PACKAGE("package"),
	IF("if"),
	ELSE("else"),
	WHILE("while"),
	DO("do"),
	FOR("for"),
	CONTINUE("continue"),
	BREAK("break"),
	RETURN("return"),
	THROW("throw"),
	TRY("try"),
	CATCH("catch"),
	FINALLY("finally"),
	SWITCH("switch"),
	CASE("case"),
	CLASS("class"),
	THIS("this"),
	EXTENDS("extends"),
	INSTANCEOF("instanceof"),
	IS("is"),
	IN("in"),
	AS("as"),
	VAR("var"),
	AUTO("auto"),
	NEW("new"),
	NATIVE("native"),
	INTERFACE("interface"),
	ENUM("enum"),
	ABSTRACT("abstract"),
	DEFAULT("default"),
	ASSERT("assert"),
	FINAL("final"),
	CONST("const"),
	I8("i8"),
	I16("i16"),
	I32("i32"),
	I64("i64"),
	F32("f32"),
	F64("f64"),
	ANY("any"),
	UNSIGNED("unsigned"),
	VOID("void"),
	EMPTY("empty"),
	NULL("null"),
	BYTE("byte"),
	SHORT("short"),
	INT("int"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double"),
	CHAR("char"),
	TRUE("true"),
	FALSE("false");

	private String keyword;

	private Keyword(String keyword) {
		this.keyword = keyword;
	}

	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789$_";

	public static final TokensFactory FACTORY = stream -> {
		for (Keyword kwd : Keyword.values()) {
			if (stream.isNext(kwd.keyword)) {
				// Make sure our keyword is not a symbol
				String lastChar = stream.getAhead(kwd.keyword.length() + 1).substring(kwd.keyword.length());
				if (lastChar.isEmpty() || ALPHABET.indexOf(lastChar) == -1) {
					stream.advanceBy(kwd.keyword.length());
					return kwd;
				}

				continue;
			}
		}

		return null;
	};

	public String getKeyword() { return keyword; }

	@Override
	public String toString() {
		return keyword;
	}
}
