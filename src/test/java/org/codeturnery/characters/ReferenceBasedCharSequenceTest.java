package org.codeturnery.characters;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReferenceBasedCharSequenceTest {

	@Test
	void testCharSequenceMethods() {
		final var threePartHelloWorldSequence = new StringSequence(3);
		threePartHelloWorldSequence.setReference(0, "Hello");
		threePartHelloWorldSequence.setReference(1, ", ");
		threePartHelloWorldSequence.setReference(2, "World!");
		final var onePartHelloWorldSequence = new StringSequence(1);
		onePartHelloWorldSequence.setReference(0, "Hello, world!");
		final var singleCharSequence = new StringSequence(1);
		singleCharSequence.setReference(0, "A");
		final var emptySequence = new StringSequence(0);
		final var largeSequence = new StringSequence(1000);
		for (int i = 0; i < 1000; i++) {
			largeSequence.setReference(i, "A");
		}

		CharSequence sequence = threePartHelloWorldSequence;

		// 1. Test length()
		assertEquals(13, threePartHelloWorldSequence.length(), "Length should be 13");

		// 2. Test charAt() with valid indices
		assertEquals('H', threePartHelloWorldSequence.charAt(0), "Char at index 0 should be 'H'");
		assertEquals('!', threePartHelloWorldSequence.charAt(12), "Char at index 12 should be '!'");

		// 3. Test charAt() with invalid indices (out of bounds)
		assertThrows(IndexOutOfBoundsException.class, () -> threePartHelloWorldSequence.charAt(-1),
				"Should throw IndexOutOfBoundsException for negative index");
		assertThrows(IndexOutOfBoundsException.class, () -> threePartHelloWorldSequence.charAt(14),
				"Should throw IndexOutOfBoundsException for index greater than length");

		// 4. Test subSequence() with valid indices
		assertEquals("Hello", threePartHelloWorldSequence.subSequence(0, 5).toString(),
				"subSequence(0, 5) should return 'Hello'");
		assertEquals(", ", threePartHelloWorldSequence.subSequence(5, 7).toString());
		assertEquals("World!", threePartHelloWorldSequence.subSequence(7, 13).toString(),
				"subSequence(7, 13) should return 'World!'");
		assertEquals("Hel", threePartHelloWorldSequence.subSequence(0, 3).toString());
		assertEquals("Wor", threePartHelloWorldSequence.subSequence(7, 10).toString());
		assertEquals("Hello, Wor", threePartHelloWorldSequence.subSequence(0, 10).toString());
		assertEquals("llo, World!", threePartHelloWorldSequence.subSequence(2, 13).toString());
		assertEquals("llo, Wor", threePartHelloWorldSequence.subSequence(2, 10).toString(),
				"subSequence(2, 10) should return 'llo, Wor'");

		// 5. Test subSequence() with invalid indices (out of bounds)
		assertThrows(IndexOutOfBoundsException.class, () -> threePartHelloWorldSequence.subSequence(-1, 5),
				"Should throw IndexOutOfBoundsException for invalid subSequence");
		assertThrows(IndexOutOfBoundsException.class, () -> threePartHelloWorldSequence.subSequence(0, 14),
				"Should throw IndexOutOfBoundsException for invalid subSequence");

		// 6. Test empty CharSequence
		assertEquals(0, emptySequence.length(), "Length of empty sequence should be 0");
		assertTrue(emptySequence.toString().isEmpty(), "Empty CharSequence should return an empty string");

		// 7. Test CharSequence with only one character
		assertEquals(1, singleCharSequence.length(), "Length of single character sequence should be 1");
		assertEquals('A', singleCharSequence.charAt(0), "Char at index 0 should be 'A'");

		// 9. Test CharSequence equality (implement equals in your MyCharSequence class
		// if necessary)
		assertTrue(threePartHelloWorldSequence.equals(threePartHelloWorldSequence), "CharSequences should be equal");
		assertFalse(threePartHelloWorldSequence.equals(onePartHelloWorldSequence),
				"CharSequences should not be equal (case sensitive)");

		// 10. Test CharSequence for String representation
		assertEquals("Hello, World!", threePartHelloWorldSequence.toString(),
				"toString() should return the correct string");

		// 11. Test CharSequence with different content (e.g., testing against a String)
		assertFalse(threePartHelloWorldSequence.equals("Hello, World!"),
				"MyCharSequence and String should not be considered equal by default");

		// 12. Test performance or large inputs
		assertEquals(1000, largeSequence.length(), "Length of large sequence should be 1000");
		assertEquals('A', largeSequence.charAt(0), "Char at index 0 should be 'A'");
		assertEquals('A', largeSequence.charAt(999), "Char at index 999 should be 'A'");

		// 13. Test CharSequence with special characters
		var specialCharSequence = new StringSequence(1);
		specialCharSequence.setReference(0, "!@#$%^&*()_+");
		assertEquals(12, specialCharSequence.length(), "Length of special characters sequence should be 12");
		assertEquals('!', specialCharSequence.charAt(0), "Char at index 0 should be '!'");
		assertEquals('+', specialCharSequence.charAt(11), "Char at index 11 should be '+'");

		// 14. Test CharSequence with a sequence of whitespace
		var whitespaceSequence = new StringSequence(1);
		whitespaceSequence.setReference(0, "    ");
		assertEquals(4, whitespaceSequence.length(), "Length of whitespace sequence should be 4");
		assertEquals(' ', whitespaceSequence.charAt(0), "Char at index 0 should be a space");
		assertEquals(' ', whitespaceSequence.charAt(3), "Char at index 3 should be a space");
	}

}