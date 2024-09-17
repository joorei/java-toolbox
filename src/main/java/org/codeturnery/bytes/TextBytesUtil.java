package org.codeturnery.bytes;

import java.nio.ByteBuffer;
import java.util.Base64;

import org.eclipse.jdt.annotation.NonNull;

public class TextBytesUtil {
	/**
	 * Byte representation of an ASCII and UTF-8 newline character.
	 * <p>
	 * Binary: <code>0b00001010</code>
	 * <p>
	 * Decimal: <code>10</code>
	 */
	private static final byte LF_BYTE = 0xA;
	/**
	 * Byte representation of an ASCII and UTF-8 carriage return character.
	 * <p>
	 * Binary: <code>0b00001101</code>
	 * <p>
	 * Decimal: <code>13</code>
	 */
	private static final byte CR_BYTE = 0xD;

	/**
	 * Returns the absolute position of the next newline (carriage return or line
	 * feed) found in <code>text</code> starting at <code>offset</code> and ending
	 * before <code>limit</code>.
	 * 
	 * @param text        UTF-8 encoded bytes.
	 * @param startOffset Must be smaller than the length of <code>text</code> and
	 *                    not be smaller than 0.
	 * @param limit       the position at and after which no checking for newline
	 *                    characters is done
	 * @return The offset at which a newline was found or <code>text.length</code>
	 *         otherwise.
	 */
	private static int getNextNewlinePosition(final byte[] text, final int startOffset, final int limit) {
		int offset = startOffset;
		while (offset < limit) {
			if (isNewlineCharacter(text[offset])) {
				return offset;
			}
			offset++;
		}
		return limit;
	}

	private static int getNextNonNewlinePosition(final byte[] text, final int startOffset, final int limit) {
		int offset = startOffset;
		while (offset < limit) {
			if (!isNewlineCharacter(text[offset])) {
				return offset;
			}
			offset++;
		}
		return limit;
	}

	/**
	 * @param character UTF-8 encoded.
	 * @return <code>true</code> if the given character is a newline (carriage
	 *         return or line feed). <code>false</code> otherwise.
	 */
	private static boolean isNewlineCharacter(final byte character) {
		// this is possible because UTF-8 is "self-synchronizing",
		// meaning that parts of multi-byte characters won't
		// match single byte characters like NL or CR
		return character == LF_BYTE || character == CR_BYTE;
	}

	/**
	 * Searches for a non-empty line inside a {@link ByteBuffer}. If one is found
	 * the position and limit of the {@link ByteBuffer} is set to the start and end
	 * of that line.
	 * 
	 * @param byteBuffer The search is limited to the array section starting at the
	 *                   current position of the {@link ByteBuffer} and ending at
	 *                   the current limit. The {@link ByteBuffer} remains unchanged
	 *                   if no non-empty line was found.
	 * @return <code>true</code> if a non-empty line was found, <code>false</code>
	 *         otherwise.
	 */
	public static boolean setBufferToNextNonEmptyLine(final ByteBuffer byteBuffer) {
		if (!byteBuffer.hasArray()) {
			throw new IllegalArgumentException("Given buffer has no accessible backing array.");
		}
		@SuppressWarnings("null")
		final byte @NonNull [] array = byteBuffer.array();
		// iterate through the given buffer to find the next position, that is *not* a
		// newline character
		int lineStart = getNextNonNewlinePosition(array, byteBuffer.position(), byteBuffer.limit());
		// As lineStart points to a non-newline character, we consider it a line, even
		// if the found position is the last position in the buffer.
		// Only if we got a position beyond the buffer, there are no characters from
		// which a line could be created.
		if (lineStart >= byteBuffer.limit()) {
			// no non-newlines until end of buffer
			return false;
		}
		// We found a non-newline character on the current position (lineStart), now we
		// want to know where the line ends. Because we already know that lineStart
		// points to a non-newline, we can continue the search at the next position
		// (lineStart + 1). This also ensures that the returned lineEnd is never less or
		// equal to lineStart.
		int lineEnd = getNextNewlinePosition(array, lineStart + 1, byteBuffer.limit());

		// Set the buffer to the found line, starting at the first non-newline character
		// and ending right after the last non-newline character, without any newline
		// characters being present in-between.
		byteBuffer.position(lineStart);
		byteBuffer.limit(lineEnd);
		return true;
	}

	public static boolean endsWithLfAndCr(final ByteBuffer fileReadBuffer) {
		return fileReadBuffer.get(fileReadBuffer.limit() - 1) == LF_BYTE
				&& fileReadBuffer.get(fileReadBuffer.limit() - 2) == CR_BYTE;

	}

	public static boolean isCarriageReturn(final byte b) {
		return b == CR_BYTE;
	}

	public static boolean isLineFeed(final byte b) {
		return b == LF_BYTE;
	}

	public static String bytesToBsae64(final byte[] bytes) {
		@SuppressWarnings("null")
		final @NonNull String result = Base64.getEncoder().encodeToString(bytes);
		return result;
	}

}
