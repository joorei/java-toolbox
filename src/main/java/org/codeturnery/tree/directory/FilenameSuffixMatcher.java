package org.codeturnery.tree.directory;

import org.codeturnery.tree.StringMatcher;
import org.codeturnery.typesystem.Iterables;
import org.eclipse.jdt.annotation.NonNull;

public class FilenameSuffixMatcher extends StringMatcher<FsGroupingNode> {

	public FilenameSuffixMatcher(final @NonNull String... suffixes) {
		super(Iterables.setOf(suffixes), Iterables.emptySet(), false);
	}

	@Override
	protected String getString(final FsGroupingNode value) {
		return value.getPath().getFileName().toString();
	}
}
