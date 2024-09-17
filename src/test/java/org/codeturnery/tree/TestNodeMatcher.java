package org.codeturnery.tree;

import org.codeturnery.typesystem.Iterables;
import org.eclipse.jdt.annotation.NonNull;

public class TestNodeMatcher extends StringMatcher<TestNode> {
	public TestNodeMatcher(final @NonNull String... suffixes) {
		super(Iterables.setOf(suffixes), Iterables.emptySet(), false);
	}

	@Override
	protected String getString(final TestNode node) {
		return node.toString();
	}
}
