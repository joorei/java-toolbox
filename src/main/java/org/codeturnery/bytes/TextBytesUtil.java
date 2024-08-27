package org.codeturnery.bytes;

import java.nio.ByteBuffer;
import java.util.Base64;

public class TextBytesUtil {
	/**
	 * Byte representation of an ASCII and UTF-8 newline character.
	 */
	private static final byte LF_BYTE = 0xA; // 0b00001010 // 10
	/**
	 * Byte representation of an ASCII and UTF-8 carriage return character.
	 */
	private static final byte CR_BYTE = 0xD; // 0b00001101 // 13
	
	/**
	 * Returns the absolute position of the next newline (carriage return or line
	 * feed) found in <code>text</code> starting at <code>offset</code> and ending
	 * before <code>limit</code>.
	 * 
	 * @param text   UTF-8 encoded bytes.
	 * @param offset Must be smaller than the length of <code>text</code> and not be
	 *               smaller than 0.
	 * @param limit
	 * @return The offset at which a newline was found or <code>text.length</code>
	 *         otherwise.
	 */
	private static int getNextNewlinePosition(final byte[] text, int offset, final int limit) {
		while (offset < limit) {
			if (isNewlineCharacter(text[offset])) {
				return offset;
			}
			offset++;
		}
		return limit;
	}
	

	private static int getNextNonNewlinePosition(final byte[] text, int offset, final int limit) {
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
		// this is possible as UTF-8 is self-synchronizing
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
		int lineStart = getNextNonNewlinePosition(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		if (lineStart >= byteBuffer.limit()) {
			// no non-newlines until end
			return false;
		}
		// this can't result in an empty line, as we increment lineStart
		int lineEnd = getNextNewlinePosition(byteBuffer.array(), lineStart + 1, byteBuffer.limit());
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
		return Base64.getEncoder().encodeToString(bytes);
	}
	
}
