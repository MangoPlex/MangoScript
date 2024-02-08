package xyz.mangostudio.mangoscript.text.lexer.stream.string;

import java.util.OptionalInt;

import xyz.mangostudio.mangoscript.text.lexer.stream.LexerStream;

public interface StringStream extends LexerStream<StringStream> {
	/**
	 * <p>
	 * Get the position of this stream.
	 * </p>
	 * 
	 * @return The position of this stream.
	 */
	public int getPosition();

	public int advanceBy(int amount);

	/**
	 * <p>
	 * Advance this stream forward to specified position.
	 * </p>
	 * 
	 * @param position The new position of this stream.
	 * @return The new position. It can be less than specified position if end of
	 *         stream is reached.
	 */
	default int advanceTo(int position) {
		advanceBy(position - getPosition());
		return getPosition();
	}

	default boolean isNext(String s) {
		String ahead = getAhead(s.length());
		return ahead.equals(s);
	}

	public MasterStringStream getMaster();

	default String getAhead(int amount) {
		return getMaster().fork$readInRange(getPosition(), amount);
	}

	default boolean isAvailable() {
		int[] cacheRange = getMaster().fork$getCacheRange(getPosition(), 1);
		getMaster().fork$readInRange(cacheRange[0], cacheRange[1]);
		OptionalInt maximum = getMaster().getMaximum();
		return maximum.isEmpty() || getPosition() < maximum.getAsInt();
	}
}
