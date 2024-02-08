package xyz.mangostudio.mangoscript.text.lexer.token;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import xyz.mangostudio.mangoscript.text.lexer.StringStreamLexer;
import xyz.mangostudio.mangoscript.text.lexer.stream.string.ReaderStringStream;

class TokenTest {
	@Test
	void testLexingSampleCode() {
		String code = """
			import "mangoscript:core";

			i32 main() {
				println("Hello world!");
			}

			class Vector2D {
				f32 x;
				f32 y;

				f32 +(Vector2D another) {
					return Vector2D(this.x + another.x, this.y + another.y);
				}
			}

			void myVectorTest() {
				Vector2D a = Vector2D(0, 0);
				Vector2D b = Vector2D(1, 1);
				println(a + b);
			}
			""";

		ReaderStringStream stream = new ReaderStringStream(code);
		StringStreamLexer lexer = new StringStreamLexer(Token.FACTORY, stream);
		System.out.println(lexer.toTokensList());
		assertTrue(true);
	}
}
