package org.codeturnery.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;

import org.codeturnery.typesystem.NonNegative;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Provides basic logic to transfer bytes and calculate digests, attempting to
 * do this in a performant way.
 */
public class BytesUtil {
	/**
	 * Attempts to read bytes from the given {@link InputStream} until the stream is
	 * empty or the specified number of bytes have been read.
	 * <p>
	 * No more than the specified number of bytes are read from the
	 * {@link InputStream} and it will not be closed by this method.
	 * <p>
	 * Depending on the size of the buffer, it will be filled differently: If it is
	 * smaller than the number of bytes to read, then it will be treated as
	 * temporary, intermediate storage and may not contain usable data when this
	 * method returns. However, if it is (after the given offset) the same size or
	 * larger than the number of bytes to read, then it will be filled with the
	 * bytes read from the stream, starting at the defined offset.
	 * 
	 * @param inputStream   The stream to read bytes from.
	 * @param buffer        The buffer used to read bytes from the stream.
	 * @param offset        The offset to respect for the given buffer. Everything
	 *                      before the offset will be treated as unusable and not be
	 *                      touched.
	 * @param bytesToRead   The attempted number of bytes to read from the stream.
	 * @param messageDigest If non-{@code null}, this digest will be filled with the
	 *                      bytes read from the {@link InputStream}..
	 * @return The actual number of bytes read from the {@link InputStream}.
	 * @throws IOException              If reading from the {@link InputStream}
	 *                                  fails.
	 * @throws IllegalArgumentException If the buffer is too small to be usable.
	 */
	public static @NonNegative int readInto(final InputStream inputStream, final byte[] buffer, final int offset,
			final int bytesToRead, final @Nullable MessageDigest messageDigest) throws IOException {
		final int usableBufferSize = buffer.length - offset;
		if (usableBufferSize < 1) {
			throw new IllegalArgumentException("Usable buffer size too small: " + usableBufferSize);
		}
		final boolean largeBuffer = usableBufferSize >= bytesToRead;
		int remainingByteCountToTransfer = bytesToRead;
		int lastLengthRead = 0;

		while (remainingByteCountToTransfer > 0) {
			final int currentBufferPosition = largeBuffer ? bytesToRead - remainingByteCountToTransfer + offset
					: offset;
			lastLengthRead = inputStream.read(buffer, currentBufferPosition,
					Math.min(usableBufferSize, remainingByteCountToTransfer));
			if (lastLengthRead <= 0) {
				break;
			}
			// this check is probably superfluous
			assert lastLengthRead <= buffer.length : lastLengthRead + " is bigger than " + usableBufferSize;
			if (messageDigest != null) {
				messageDigest.update(buffer, currentBufferPosition, lastLengthRead);
			}
			remainingByteCountToTransfer -= lastLengthRead;
		}

		final int actualReadCount = bytesToRead - remainingByteCountToTransfer;
		if (actualReadCount < 0 || actualReadCount > bytesToRead) {
			throw new IllegalStateException("Read unexpected number of bytes: " + actualReadCount);
		}

		return actualReadCount;
	}

	/**
	 * Adds the defined number of bytes from the given input to the
	 * {@link MessageDigest}, calculates the digest and writes it into the given
	 * output.
	 * 
	 * @param digest      {@link MessageDigest} to use for the calculation. Will not
	 *                    be reset before use, take care of that yourself. Will be
	 *                    reset after usage.
	 * @param inputLength the length of the given <code>input</code> that is allowed
	 *                    to be used, starting at the beginning of the buffer
	 * @param output      the buffer into which the digest shall be written,
	 *                    starting at the beginning
	 * @param input       the buffer containing the data from which the digest shall
	 *                    be calculated, only the first <code>inputLength</code>
	 *                    bytes are used
	 * @throws DigestException thrown if an error occurs during the digest
	 *                         calculation
	 */
	public static void calculateDigest(final MessageDigest digest, final int inputLength, final byte[] output,
			final byte[] input) throws DigestException {
		if (input.length < inputLength) {
			throw new IllegalArgumentException("given input is smaller than given length");
		}
		digest.update(input, 0, inputLength);
		writeDigestInto(digest, output);
	}

	/**
	 * Calculates the digest from the given {@link MessageDigest} instance and
	 * writes the result into the given output array.
	 * 
	 * @param digest the source of the digest calculation
	 * @param output the target to write the digest into
	 * @throws DigestException       thrown if an error occurs during the digest
	 *                               calculation
	 * @throws IllegalStateException thrown if the digest calculation resulted in
	 *                               bytes not exactly filling the output array
	 */
	public static void writeDigestInto(final MessageDigest digest, final byte[] output) throws DigestException {
		final int writtenBytes = digest.digest(output, 0, output.length);
		if (writtenBytes != output.length) {
			throw new IllegalStateException("Digest length does not size of expected output.");
		}
	}
}
