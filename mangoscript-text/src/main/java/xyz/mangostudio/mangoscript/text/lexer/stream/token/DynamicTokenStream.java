package xyz.mangostudio.mangoscript.text.lexer.stream.token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import xyz.mangostudio.mangoscript.text.lexer.token.Token;

public class DynamicTokenStream implements TokenStream {
	protected int position = 0;
	private Supplier<Token> supplier;
	private List<Token> buffer = new ArrayList<>();
	private int bufferFirstPosition = 0;
	private boolean endOfStream = false;
	protected Set<TokenStream> forks = new HashSet<>();

	public DynamicTokenStream(Supplier<Token> supplier) {
		this.supplier = supplier;
		this.forks.add(this);
		fillBuffer(1);
	}

	private int[] getCacheRange(int overhead) {
		int min = position, max = position + overhead;

		for (TokenStream fork : forks) {
			min = Math.min(min, fork.getPosition());
			max = Math.max(max, fork.getPosition());
		}

		return new int[] { min, max };
	}

	private void fillBuffer(int overhead) {
		int[] cacheRange = getCacheRange(overhead);
		int bufferSize = cacheRange[1] - bufferFirstPosition;

		while (!endOfStream && buffer.size() < bufferSize) {
			Token token = supplier.get();

			if (token == null) {
				endOfStream = true;
				break;
			}

			buffer.add(token);
		}

		for (int i = 0; i < cacheRange[0] - bufferFirstPosition; i++) {
			buffer.remove(0);
			bufferFirstPosition++;
		}
	}

	public Token getAt(int position) {
		fillBuffer(position - this.position + 1);
		int index = position - bufferFirstPosition;
		if (index < 0) return null;
		if (index >= buffer.size()) return null;
		return buffer.get(index);
	}

	@Override
	public boolean hasNext() {
		return getAt(position) != null;
	}

	@Override
	public TokenStream fork() {
		ForkedTokenStream fork = new ForkedTokenStream(this);
		forks.add(fork);
		return fork;
	}

	@Override
	public void join() {}

	@Override
	public void destroy() {}

	@Override
	public Token getAhead() { return getAt(position); }

	@Override
	public boolean skipToken() {
		Token ahead = getAhead();

		if (ahead != null) {
			position++;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getPosition() { return position; }
}
