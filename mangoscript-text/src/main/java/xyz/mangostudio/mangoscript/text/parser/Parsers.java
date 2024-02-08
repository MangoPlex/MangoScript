package xyz.mangostudio.mangoscript.text.parser;

public class Parsers {
	public static final Parser ALL = Parser.combine(
		// Statements
		ScopeStatementParser.PARSER,
		ReturnStatementParser.PARSER,
		ConditionalStatementParser.PARSER,
		LabeledLoopingStatementParser.PARSER,
		WhileStatementParser.PARSER,
		DoWhileStatementParser.PARSER,
		ForStatementParser.PARSER,
		BreakStatementParser.PARSER,
		DefineStatementParser.PARSER,
		EvaluateStatementParser.PARSER,

		// Expressions
		ExpressionChainParser.PARSER);
}
