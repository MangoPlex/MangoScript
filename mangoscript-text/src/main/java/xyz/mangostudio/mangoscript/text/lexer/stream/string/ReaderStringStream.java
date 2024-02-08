package xyz.mangostudio.mangoscript.text.lexer.stream.string;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

public class ReaderStringStream implements MasterStringStream {
	private static final int MAX_BUFFER_SIZE = 16; // TODO
	private Reader reader;
	private int position = 0, lastMinPosition = 0, maximum = -1;
	private Map<Integer, char[]> buffers = new HashMap<>(); // bufferIndex => char[]
	private Set<StringStream> forks = new HashSet<>();
	private boolean endOfStream = false;

	public ReaderStringStream(Reader reader) {
		this.reader = reader;
		this.forks.add(this);
	}

	public ReaderStringStream(String string) {
		this(new StringReader(string));
	}

	private void fillBuffers(int fromPosition, int toPosition) {
		int fromBuffer = fromPosition / MAX_BUFFER_SIZE;
		int toBuffer = toPosition / MAX_BUFFER_SIZE;

		for (int buffer = fromBuffer; buffer <= toBuffer; buffer++) {
			if (endOfStream) break;
			char[] cs = buffers.get(buffer);

			if (cs == null) {
				cs = new char[MAX_BUFFER_SIZE];
				int charRead = 0;

				try {
					while (charRead < MAX_BUFFER_SIZE) {
						int charReadThisPart = reader.read(cs, charRead, MAX_BUFFER_SIZE - charRead);
						if (charReadThisPart == -1) {
							endOfStream = true;
							maximum = buffer * MAX_BUFFER_SIZE + charRead;
							break;
						}

						charRead += charReadThisPart;
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				buffers.put(buffer, cs);
			}
		}

		int removeFrom = lastMinPosition / MAX_BUFFER_SIZE;
		lastMinPosition = fromPosition;
		for (int buffer = removeFrom; buffer < fromBuffer; buffer++) buffers.remove(buffer);
	}

	@Override
	public int getPosition() { return position; }

	@Override
	public int advanceBy(int amount) {
		int[] cacheRange = fork$getCacheRange(position, amount);
		fillBuffers(cacheRange[0], cacheRange[1]);

		if (maximum == -1) {
			position += amount;
			return amount;
		}

		int last = position;
		position = Math.min(maximum, position + amount);
		return position - last;
	}

	@Override
	public String fork$readInRange(int position, int amount) {
		int[] cacheRange = fork$getCacheRange(position, amount);
		fillBuffers(cacheRange[0], cacheRange[1]);

		int readFrom = position;
		int readTo = readFrom + amount;
		if (maximum != -1) readTo = Math.min(readTo, maximum);
		char[] cs = new char[readTo - readFrom];

		int fromBuffer = readFrom / MAX_BUFFER_SIZE;
		int toBuffer = readTo / MAX_BUFFER_SIZE;

		// Start
		int startBufferPos = readFrom - fromBuffer * MAX_BUFFER_SIZE;
		int startLength = Math.min(cs.length, MAX_BUFFER_SIZE - startBufferPos);
		char[] buffer = buffers.get(fromBuffer);
		System.arraycopy(buffer, startBufferPos, cs, 0, startLength);

		// Middle
		for (int i = fromBuffer + 1; i < toBuffer; i++) {
			int destOffset = startLength + (i - fromBuffer - 1) * MAX_BUFFER_SIZE;
			buffer = buffers.get(i);
			System.arraycopy(buffer, 0, cs, destOffset, MAX_BUFFER_SIZE);
		}

		// End
		if (fromBuffer != toBuffer) {
			int endLength = readTo - toBuffer * MAX_BUFFER_SIZE;
			int destOffset = startLength + (toBuffer - fromBuffer - 1) * MAX_BUFFER_SIZE;
			buffer = buffers.get(toBuffer);
			System.arraycopy(buffer, 0, cs, destOffset, endLength);
		}

		return String.valueOf(cs);
	}

	@Override
	public int[] fork$getCacheRange(int position, int overhead) {
		int min = position, max = position + overhead;

		for (StringStream s : forks) {
			min = Math.min(s.getPosition(), min);
			max = Math.max(s.getPosition(), max);
		}

		return new int[] { min, max };
	}

	@Override
	public StringStream fork() {
		ForkedStringStream fork = new ForkedStringStream(this);
		forks.add(fork);
		return fork;
	}

	@Override
	public void join() {
		// Do nothing.
	}

	@Override
	public void destroy() {
		try {
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void registerFork(StringStream stream) {
		forks.add(stream);
	}

	@Override
	public void destroyFork(StringStream stream) {
		forks.remove(stream);
	}

	@Override
	public OptionalInt getMaximum() { return maximum != -1 ? OptionalInt.of(maximum) : OptionalInt.empty(); }
}
