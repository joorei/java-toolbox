package org.codeturnery.bytes;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;

public class BytesUtil {
	/**
	 * 
	 * @param inputStream
	 * @param buffer
	 * @param messageDigest
	 * @throws IOException
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
	 * @param inputBufferLength
	 * @param outputBuffer
	 * @param inputBuffer
	 * @throws DigestException
	 */
	public static void calculateDigest(final MessageDigest digest, final int inputBufferLength,
			final byte[] outputBuffer, final byte[] inputBuffer) throws DigestException {
		if (inputBuffer.length < inputBufferLength) {
			throw new IllegalArgumentException("given input is smaller than given length");
		}
		digest.update(inputBuffer, 0, inputBufferLength);
		writeDigestInto(digest, outputBuffer);
	}

	public static void writeDigestInto(final MessageDigest digest, final byte[] output) throws DigestException {
		if (digest.digest(output, 0, output.length) != output.length) {
			throw new IllegalStateException("strange behavior in digest creation");
		}
	}
}
