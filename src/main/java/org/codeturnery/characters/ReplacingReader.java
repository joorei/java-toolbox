package org.codeturnery.characters;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Passes along the characters from an underlying {@link Reader}, but replaces
 * parts on the fly.
 * <p>
 * While characters are requested from an instance, the implementation will
 * check for a special start-character. When it is encountered, the underlying
 * reader will be read until the corresponding stop-character is encountered.
 * The characters between those two will be considered a key and instead of
 * passing them to the caller, the characters of a corresponding value are used
 * instead.
 */
public class ReplacingReader extends Reader {
	/**
	 * TODO: add a method similar to {@link Reader#read(char[], int, int)},
	 * implement it efficently and use it instead of {@link #getChar()} where
	 * appropriate.
	 */
	public interface Replacement extends Closeable {
		/**
		 * @return The next character from the replacement or -1 if the end was reached.
		 * @throws IOException
		 */
		int getChar() throws IOException;
	}

	static private class StringReplacement implements Replacement {
		private int position = 0;
		private final String value;

		protected StringReplacement(final String value) {
			this.value = value;
		}

		@Override
		public int getChar() {
			if (this.position < this.value.length()) {
				return this.value.charAt(this.position++);
			}
			return -1;
		}

		@Override
		public void close() throws IOException {
			// nothing to do
		}
	}

	static private class CharSequenceReplacement implements Replacement {
		private int position = 0;
		private final CharSequence value;

		protected CharSequenceReplacement(final CharSequence value) {
			this.value = value;
		}

		@Override
		public int getChar() {
			if (this.position < this.value.length()) {
				return this.value.charAt(this.position++);
			}
			return -1;
		}

		@Override
		public void close() throws IOException {
			// nothing to do
		}
	}

	static private class ReaderReplacement implements Replacement {
		private final Reader reader;

		protected ReaderReplacement(Reader reader) {
			this.reader = reader;
		}

		@Override
		public void close() throws IOException {
			this.reader.close();
		}

		@Override
		public int getChar() throws IOException {
			return this.reader.read();
		}
	}

	private @Nullable Replacement currentReplacement;
	private final Reader underlyingReader;
	private final Function<String, Replacement> replacementProvider;
	private final char startChar;
	private final char stopChar;
	private final StringBuilder keyBuilder = new StringBuilder();

	public static ReplacingReader fromCharSequences(Reader underlyingReader, char start, char end,
			Function<String, CharSequence> replacementProvider) {
		return new ReplacingReader(underlyingReader, start, end,
				key -> new CharSequenceReplacement(replacementProvider.apply(key)));
	}

	public static ReplacingReader fromReaders(Reader underlyingReader, char start, char end,
			Function<String, Reader> replacementProvider) {
		return new ReplacingReader(underlyingReader, start, end,
				key -> new ReaderReplacement(replacementProvider.apply(key)));
	}

	public static ReplacingReader fromStrings(Reader underlyingReader, char start, char end,
			Function<String, String> replacementProvider) {
		return new ReplacingReader(underlyingReader, start, end,
				key -> new StringReplacement(replacementProvider.apply(key)));
	}

	public ReplacingReader(Reader underlyingReader, char start, char end,
			Function<String, Replacement> replacementProvider) {
		this.underlyingReader = underlyingReader;
		this.replacementProvider = replacementProvider;
		this.startChar = start;
		this.stopChar = end;
	}

	@Override
	public int read() throws IOException {
		return this.currentReplacement == null ? readFromReader() : readFromReplacement();
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (off < 0) {
			throw new IndexOutOfBoundsException(off);
		}
		if (len < 0 || len > cbuf.length - off) {
			throw new IndexOutOfBoundsException(len);
		}

		if (len == 0) {
			return 0;
		}
		int charsRead = 0;
		while (charsRead < len) {
			int ch = read();
			if (ch == -1) {
				break;
			}
			cbuf[off + charsRead] = (char) ch;
			charsRead++;
		}
		return (charsRead == 0 && len > 0) ? -1 : charsRead;
	}

	@Override
	public void close() throws IOException {
		this.underlyingReader.close();
	}

	/**
	 * Read the next character from {@link #currentReplacement}. If there are no
	 * more characters, set it to {@code null} and read from
	 * {@link #underlyingReader} instead.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected int readFromReplacement() throws IOException {
		final int character = this.currentReplacement.getChar();
		if (character == -1) {
			this.currentReplacement = null;
			return this.underlyingReader.read();
		}
		return character;
	}

	/**
	 * Read characters from {@link #underlyingReader} until {@link #stopChar} is
	 * encountered. From the read characters (excluding {@link #stopChar}) a
	 * {@link String} is created, which is passed to {@link #replacementProvider} to
	 * retrieve the replacement value. This value is then used to update
	 * {@link #currentReplacement}. I.e. when this method returns
	 * {@link #currentReplacement} will not be {@code null}.
	 * 
	 * @throws IOException
	 */
	protected void readAheadTillStop() throws IOException {
		int character;
		while ((character = this.underlyingReader.read()) != -1) {
			if (character == this.stopChar) {
				final String key = this.keyBuilder.toString();
				this.keyBuilder.setLength(0);
				this.currentReplacement = this.replacementProvider.apply(key);
				return;
			}
			this.keyBuilder.append((char) character);
		}
		throw new IllegalStateException("No stop character found after start character.");
	}

	/**
	 * Read the next character from {@link #underlyingReader}. If it is not
	 * {@link #startChar} then it is simply returned. But if it is
	 * {@link #startChar}, then the {@link #underlyingReader} is read until
	 * {@link #stopChar} is encountered. The read characters are used to retrieve a
	 * replacement value and the first character from the replacement is returned.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected int readFromReader() throws IOException {
		final int character = this.underlyingReader.read();
		if (character != this.startChar) {
			return character;
		}
		readAheadTillStop();
		return readFromReplacement();
	}
}
