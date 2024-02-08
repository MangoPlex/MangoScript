package xyz.mangostudio.mangoscript.text.lexer.stream;

import java.io.InputStream;

/**
 * <p>
 * Lexer streams read symbols continuously, without ability to seek backward.
 * This is important for performance, as we don't want to store all symbols in
 * the memory then process each one; we only need to find next symbol when we
 * need it.
 * </p>
 * <p>
 * Stream can be forked, which simulates marking in {@link InputStream}. Unlike
 * marking, forking allows you to keep track of multiple "marks".
 * </p>
 * 
 * @param <T>
 */
public interface LexerStream<T extends LexerStream<T>> {
	/**
	 * <p>
	 * Fork this stream. A new fork can be processed and then either join with main
	 * stream, or destroy if failed.
	 * </p>
	 * <p>
	 * A stream must be destroyed to avoid potential high memory usage due to
	 * caching. This is because {@link LexerStream} implementations cache symbols
	 * from branches with lowest position to highest. For example, if a branch stuck
	 * at position 0, every time a new symbol is obtained, the cache grow by 1.
	 * </p>
	 * 
	 * @return A forked stream.
	 * @see #join()
	 * @see #destroy()
	 */
	public T fork();

	public void join();

	public void destroy();
}
