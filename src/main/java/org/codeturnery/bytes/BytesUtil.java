package org.codeturnery.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;

public class BytesUtil {
	/**
	 * Read the given stream into the given buffer until it is full or the stream is
	 * empty and fill the given {@link MessageDigest} instance at the same time with
	 * the data written into the buffer, to allow to calculate a digest from the
	 * written data.
	 * <p>
	 * I.e. if the stream contains more data than the buffer can hold, the buffer is
	 * completely filled and no more data read from the stream.
	 * 
	 * @param inputStream   the stream to read data from
	 * @param buffer        the buffer to fill
	 * @param messageDigest the digest to fill with the same data that was written
	 *                      into the buffer
	 * @throws IOException thrown if reading from the stream fails for some reason
	 */
	public static void readInto(final InputStream inputStream, final byte[] buffer, final MessageDigest messageDigest)
			throws IOException {
		int remainingByteCountToTransfer = buffer.length;
		int lastLengthRead = 0;
		/*
		 * a single InputStream.read invocation may not return the requested length,
		 * even if the buffer is sufficient, hence we need to call it multiple times
		 */
		while (remainingByteCountToTransfer > 0) {
			lastLengthRead = inputStream.read(buffer, 0, remainingByteCountToTransfer);
			if (lastLengthRead != -1) {
				assert lastLengthRead > 0;
				assert lastLengthRead <= buffer.length : lastLengthRead + " is bigger than " + buffer.length;
				messageDigest.update(buffer, 0, lastLengthRead);
				remainingByteCountToTransfer -= lastLengthRead;
			} else {
				lastLengthRead = 0;
			}
		}
		assert remainingByteCountToTransfer >= 0 : "read more data from input stream than expected";
		assert inputStream.read(new byte[1]) == -1 : "missing bytes?";
	}

	/**
	 * Reads the given stream into the given buffer until it is full or the stream
	 * is empty.
	 * <p>
	 * I.e. if the stream contains more data than the buffer can hold, the buffer is
	 * completely filled and no more data read from the stream.
	 * 
	 * @param inputStream the stream to read
	 * @param buffer      the buffer to fill
	 * @throws IOException thrown if reading from the stream fails for some reason
	 */
	public static void readInto(final InputStream inputStream, final byte[] buffer) throws IOException {
		int remainingByteCountToTransfer = buffer.length;
		int lastLengthRead = 0;
		/*
		 * a single InputStream.read invocation may not return the requested length,
		 * even if the buffer is sufficient, hence we need to call it multiple times
		 */
		while (remainingByteCountToTransfer > 0) {
			lastLengthRead = inputStream.read(buffer, 0, remainingByteCountToTransfer);
			if (lastLengthRead != -1) {
				assert lastLengthRead > 0;
				assert lastLengthRead <= buffer.length : lastLengthRead + " is bigger than " + buffer.length;
				remainingByteCountToTransfer -= lastLengthRead;
			} else {
				lastLengthRead = 0;
			}
		}
		assert remainingByteCountToTransfer >= 0 : "read more data from input stream than expected";
		assert inputStream.read(new byte[1]) == -1 : "missing bytes?";
	}

	/**
	 * 
	 * @param digest            {@link MessageDigest} to use for the calculation.
	 *                          Will not be reset before use, take care of that
	 *                          yourself. Will be reset after usage.
	 * @param inputBufferLength the length of the given <code>inputBuffer</code>
	 *                          that is allowed to be used, starting at the
	 *                          beginning of the buffer
	 * @param outputBuffer      the buffer into which the digest shall be written,
	 *                          starting at the beginning
	 * @param inputBuffer       the buffer containing the data from which the digest
	 *                          shall be calculated, only the first
	 *                          <code>inputBufferLength</code> bytes are used
	 * @throws DigestException thrown if an error occurs during the digest
	 *                         calculation
	 */
	public static void calculateDigest(final MessageDigest digest, final int inputBufferLength,
			final byte[] outputBuffer, final byte[] inputBuffer) throws DigestException {
		if (inputBuffer.length < inputBufferLength) {
			throw new IllegalArgumentException("given input is smaller than given length");
		}
		digest.update(inputBuffer, 0, inputBufferLength);
		writeDigestInto(digest, outputBuffer);
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
		if (digest.digest(output, 0, output.length) != output.length) {
			throw new IllegalStateException("strange behavior in digest creation");
		}
	}
}
