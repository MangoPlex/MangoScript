package xyz.mangostudio.mangoscript.runtime.testing;

import java.io.StringReader;

import xyz.mangostudio.mangoscript.binary.expr.Expression;
import xyz.mangostudio.mangoscript.binary.stmt.Statement;
import xyz.mangostudio.mangoscript.runtime.module.ModuleContext;
import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.DynamicTokenStream;
import xyz.mangostudio.mangoscript.text.lexer.stream.token.TokenStream;
import xyz.mangostudio.mangoscript.text.lexer.token.Token;
import xyz.mangostudio.mangoscript.text.parser.ModuleParser;
import xyz.mangostudio.mangoscript.text.parser.Parsers;

public class TestUtils {
	public static TokenStream tokensOf(String code) {
		return new DynamicTokenStream(new StringStreamLexer(Token.FACTORY, new ReaderStringStream(new StringReader(code)))::nextToken);
	}

	public static Statement parseStatement(String code) {
		return Parsers.ALL.parseStatement(tokensOf(code));
	}

	public static Expression parseExp(String code) {
		return Parsers.ALL.parseExpression(tokensOf(code));
	}

	public static ModuleContext parseModule(String code) {
		return ModuleContext.create().load(ModuleParser.parse(tokensOf(code), Parsers.ALL), null);
	}
}
