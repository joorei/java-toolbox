package org.codeturnery.tree;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class GroupPredicateHasherTest {

	@Test
	void test() {
		final var config = new Config();
		final var grouper = new Grouper<>(config.getPredicates());
		final var hasher = new GroupPredicateHasher<>(grouper, config.getHashApproaches());

		assertEquals("[77]", Arrays.toString(hasher.reduceDuplicatesTo(new int[] {77}, 2)));
		assertEquals("[77, 77]", Arrays.toString(hasher.reduceDuplicatesTo(new int[] {77, 77}, 2)));
		assertEquals("[77]", Arrays.toString(hasher.reduceDuplicatesTo(new int[] {77, 77}, 1)));
		assertEquals("[77, 88]", Arrays.toString(hasher.reduceDuplicatesTo(new int[] {77, 88, 88}, 1)));
	}

}
