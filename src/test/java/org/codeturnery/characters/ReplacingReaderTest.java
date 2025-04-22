package org.codeturnery.characters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Test;

public class ReplacingReaderTest {
	@Test
	void test() throws IOException {
		final var reader = ReplacingReader.fromStrings(new StringReader("a{foo}c"), '{', '}', key -> {
			assertEquals("foo", key);
			return "b";
		});
		assertEquals('a', reader.read());
		assertEquals('b', reader.read());
		assertEquals('c', reader.read());
		assertEquals(-1, reader.read());
		assertEquals(-1, reader.read());
	}
}
