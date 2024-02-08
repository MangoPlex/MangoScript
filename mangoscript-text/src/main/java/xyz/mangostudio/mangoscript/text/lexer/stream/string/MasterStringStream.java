package xyz.mangostudio.mangoscript.text.lexer.stream.string;

import java.util.OptionalInt;

public interface MasterStringStream extends StringStream {
	@Override
	default MasterStringStream getMaster() { return this; }

	public void registerFork(StringStream stream);

	public void destroyFork(StringStream stream);

	public OptionalInt getMaximum();

	public String fork$readInRange(int position, int amount);

	public int[] fork$getCacheRange(int position, int overhead);

	@Override
	default String getAhead(int amount) {
		return fork$readInRange(getPosition(), amount);
	}

	@Override
	default boolean isAvailable() {
		int[] cacheRange = fork$getCacheRange(getPosition(), 1);
		fork$readInRange(cacheRange[0], cacheRange[1]);
		OptionalInt maximum = getMaximum();
		return maximum.isEmpty() || getPosition() < maximum.getAsInt();
	}
}
