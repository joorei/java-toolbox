package org.codeturnery.tree.directory;

import java.util.function.Predicate;

import org.codeturnery.tree.StringMatcher;
import org.codeturnery.typesystem.Iterables;
import org.eclipse.jdt.annotation.NonNull;

/**
 * {@link Predicate} implementation matching specific {@link FsGroupingNode}
 * instances. The path represented by the {@link FsGroupingNode} will be
 * retrieved and the contained file name matched against the suffixes given on
 * initialization. If any suffix matches the file name, then the predicate
 * matches the node.
 */
public class FilenameSuffixMatcher extends StringMatcher<FsGroupingNode> {

	/**
	 * @param suffixes the suffixes to test for the filename of a given
	 *                 {@link FsGroupingNode}
	 */
	public FilenameSuffixMatcher(final @NonNull String... suffixes) {
		super(Iterables.setOf(suffixes), Iterables.emptySet(), false);
	}

	@Override
	protected String getString(final FsGroupingNode value) {
		return value.getPath().getFileName().toString();
	}
}
