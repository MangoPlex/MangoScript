package xyz.mangostudio.mangoscript.text.lexer.stream.string;

import java.util.OptionalInt;

public class ForkedStringStream implements StringStream {
	private StringStream from;
	private int position;

	public ForkedStringStream(StringStream from) {
		this.from = from;
		this.position = from.getPosition();
	}

	@Override
	public int advanceBy(int amount) {
		int[] cacheRange = getMaster().fork$getCacheRange(position, amount);
		getMaster().fork$readInRange(cacheRange[0], cacheRange[1]);
		OptionalInt maximum = getMaster().getMaximum();

		if (maximum.isEmpty()) {
			position += amount;
			return amount;
		}

		int last = position;
		position = Math.min(maximum.getAsInt(), position + amount);
		return position - last;
	}

	@Override
	public StringStream fork() {
		ForkedStringStream fork = new ForkedStringStream(this);
		from.getMaster().registerFork(fork);
		return fork;
	}

	@Override
	public void join() {
		from.advanceTo(position);
		destroy();
	}

	@Override
	public void destroy() {
		from.getMaster().destroyFork(this);
	}

	@Override
	public int getPosition() { return position; }

	@Override
	public MasterStringStream getMaster() { return from.getMaster(); }
}
