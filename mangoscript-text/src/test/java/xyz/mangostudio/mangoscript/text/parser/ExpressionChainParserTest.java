package xyz.mangostudio.mangoscript.text.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.expr.LocalExpression;
import xyz.mangostudio.mangoscript.binary.expr.Operator;
import xyz.mangostudio.mangoscript.binary.expr.PropertyGetExpression;
import xyz.mangostudio.mangoscript.binary.expr.ThisExpression;
import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.DynamicTokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;

class ExpressionChainParserTest {
	@Test
	void testSimple() {
		String code = "this.x = this.y";
		DynamicTokenStream stream = new DynamicTokenStream(new StringStreamLexer(Token.FACTORY, new ReaderStringStream(code))::nextToken);
		Expression expression = ExpressionChainParser.PARSER.parseExpression(stream);
		assertEquals(ThisExpression.THIS.getProperty("x").setTo(ThisExpression.THIS.getProperty("y")), expression);
	}

	@Test
	void testOperators() {
		String code = "this.x + this.y - this.z * this.w - this.a + this.b";
		DynamicTokenStream stream = new DynamicTokenStream(new StringStreamLexer(Token.FACTORY, new ReaderStringStream(code))::nextToken);
		Expression expression = ExpressionChainParser.PARSER.parseExpression(stream);

		PropertyGetExpression x = ThisExpression.THIS.getProperty("x");
		PropertyGetExpression y = ThisExpression.THIS.getProperty("y");
		PropertyGetExpression z = ThisExpression.THIS.getProperty("z");
		PropertyGetExpression w = ThisExpression.THIS.getProperty("w");
		PropertyGetExpression a = ThisExpression.THIS.getProperty("a");
		PropertyGetExpression b = ThisExpression.THIS.getProperty("b");

		assertEquals(x.op(Operator.ADD, y).op(Operator.SUBTRACT, z.op(Operator.MULTIPLY, w)).op(Operator.SUBTRACT, a)
			.op(Operator.ADD, b), expression);
	}

	@Test
	void testOperatorsComplex() {
		String code = "a * ((b + d) - c)";
		DynamicTokenStream stream = new DynamicTokenStream(new StringStreamLexer(Token.FACTORY, new ReaderStringStream(code))::nextToken);
		Expression expression = ExpressionChainParser.PARSER.parseExpression(stream);

		PropertyGetExpression a = LocalExpression.LOCAL.getProperty("a");
		PropertyGetExpression b = LocalExpression.LOCAL.getProperty("b");
		PropertyGetExpression c = LocalExpression.LOCAL.getProperty("c");
		PropertyGetExpression d = LocalExpression.LOCAL.getProperty("d");

		assertEquals(a.op(Operator.MULTIPLY, b.op(Operator.ADD, d).op(Operator.SUBTRACT, c)), expression);
	}
}
