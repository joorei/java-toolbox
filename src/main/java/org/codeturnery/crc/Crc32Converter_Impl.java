package org.codeturnery.crc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Basic implementation of a {@link Crc32Converter} not optimized for object
 * re-usage or performance.
 */
public class Crc32Converter_Impl implements Crc32Converter {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int crcToMaskableInt(final long crcLong) {
		return (int) crcLong;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long crcToLong(final byte[] crcBytes) {
		return ByteBuffer.wrap(crcBytes).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xffffffffL;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("null")
	@Override
	public byte @NonNull[] crcToBytes(final long crcLong) {
		return ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN).putInt((int) crcLong).array();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int crcToInt(final long crcLong) {
		return Integer.reverseBytes((int) crcLong);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("null")
	@Override
	public byte @NonNull[] crcToBytes(final int crcInt) {
		// uses ByteOrder.BIG_ENDIAN by default
		return ByteBuffer.allocate(Integer.BYTES).putInt(crcInt).array();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long crcToLong(final int crcInt) {
		return Integer.reverseBytes(crcInt) & 0xffffffffL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringBuilder toString(final byte[] bytes) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			final String string = String.format("%5d", Byte.valueOf(bytes[i]));
			stringBuilder.append(string);
		}
		return stringBuilder;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("null")
	@Override
	public @NonNull StringBuilder toString(final int bytes) {
		return toString(ByteBuffer.allocate(Integer.BYTES).putInt(bytes).array());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("null")
	@Override
	public @NonNull StringBuilder toString(final long bytes) {
		return toString(ByteBuffer.allocate(Long.BYTES).putLong(bytes).array());
	}

}
