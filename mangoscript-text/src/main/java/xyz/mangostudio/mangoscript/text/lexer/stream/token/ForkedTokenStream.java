package xyz.mangostudio.mangoscript.text.lexer.stream.token;

import xyz.mangostudio.mangoscript.text.lexer.token.Token;

public class ForkedTokenStream implements TokenStream {
	protected int position;
	private DynamicTokenStream root;
	private ForkedTokenStream from;

	public ForkedTokenStream(DynamicTokenStream root) {
		this.root = root;
		this.position = root.position;
	}

	public ForkedTokenStream(DynamicTokenStream root, ForkedTokenStream from) {
		this.root = root;
		this.from = from;
		this.position = from.position;
	}

	@Override
	public TokenStream fork() {
		ForkedTokenStream fork = new ForkedTokenStream(root, this);
		root.forks.add(fork);
		return fork;
	}

	@Override
	public void join() {
		if (from != null) from.position = position;
		else root.position = position;
		destroy();
	}

	@Override
	public void destroy() {
		root.forks.remove(this);
	}

	@Override
	public boolean hasNext() {
		return getAhead() != null;
	}

	@Override
	public Token getAhead() { return root.getAt(position); }

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
