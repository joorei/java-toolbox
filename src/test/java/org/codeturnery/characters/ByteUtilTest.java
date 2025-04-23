package org.codeturnery.characters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.codeturnery.bytes.BytesUtil;
import org.junit.jupiter.api.Test;

public class ByteUtilTest {
	private static final String TEST_DATA = "Foobar";
	private static final int LAST_CHAR = TEST_DATA.charAt(TEST_DATA.length() - 1);
	@Test
	void testReadIntoWithSmallBuffer() throws IOException {
		final byte[] inputBytes = TEST_DATA.getBytes();
		try (final InputStream is = new ByteArrayInputStream(inputBytes)) {
			final byte[] buffer = new byte[2];
			final int count = BytesUtil.readInto(is, buffer, 0, inputBytes.length, null);
			assertEquals(inputBytes.length, count);
			assertEquals(-1, is.read());
		}
	}
	@Test
	void testReadIntoWithLargeBuffer() throws IOException {
		final byte[] inputBytes = TEST_DATA.getBytes();
		try (final InputStream is = new ByteArrayInputStream(inputBytes)) {
			final byte[] buffer = new byte[inputBytes.length];
			final int count = BytesUtil.readInto(is, buffer, 0, inputBytes.length, null);
			assertEquals(inputBytes.length, count);
			assertEquals(-1, is.read());
			assertTrue(Arrays.equals(inputBytes, buffer));
		}
	}
	@Test
	void testReadIntoWithLargerBufferWithOffset() throws IOException {
		final byte[] inputBytes = TEST_DATA.getBytes();
		final int offset = 3;
		final int unused = 10;
		try (final InputStream is = new ByteArrayInputStream(inputBytes)) {
			final byte[] buffer = new byte[offset + inputBytes.length + unused];
			final int count = BytesUtil.readInto(is, buffer, offset, inputBytes.length, null);
			assertEquals(inputBytes.length, count);
			assertEquals(-1, is.read());
			assertTrue(Arrays.equals(inputBytes, Arrays.copyOfRange(buffer, offset, count + offset)));
		}
	}
	
	@Test
	void testReadPartWithLargerBufferWithOffset() throws IOException {
		final byte[] inputBytes = TEST_DATA.getBytes();
		final int offset = 3;
		final int unused = 10;
		final int toRead = inputBytes.length - 1;
		try (final InputStream is = new ByteArrayInputStream(inputBytes)) {
			final byte[] buffer = new byte[offset + inputBytes.length + unused];
			final int count = BytesUtil.readInto(is, buffer, offset, toRead, null);
			assertEquals(toRead, count);
			assertEquals(LAST_CHAR, is.read());
			assertTrue(Arrays.equals(Arrays.copyOfRange(inputBytes, 0, toRead), Arrays.copyOfRange(buffer, offset, count + offset)));
		}
	}
}
