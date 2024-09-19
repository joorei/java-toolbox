package org.codeturnery.tree.directory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.codeturnery.tree.AbstractChildableNode;
import org.codeturnery.tree.TreeNodeInterface;
import org.codeturnery.typesystem.Optionals;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Represents a single file or directory.
 */
public class FsGroupingNode extends AbstractChildableNode<FsGroupingNode> implements TreeNodeInterface<FsGroupingNode> {
	private final Path path;
	private final @Nullable FsGroupingNode parent;

	/**
	 * Create a node for which no parent exists (i.e. a root node).
	 * 
	 * @param path the path to a file or directory in the file system
	 */
	public FsGroupingNode(final Path path) {
		this.path = path;
		this.parent = null;
	}

	/**
	 * Create a child node with the given parent.
	 * <p>
	 * <strong>The child will not automatically be added to the parent
	 * node.</strong>
	 * 
	 * @param path   the path to a file or directory in the file system
	 * @param parent the parent of this node
	 */
	private FsGroupingNode(final Path path, final FsGroupingNode parent) {
		this.path = path;
		this.parent = parent;
	}

	/**
	 * @return the path in a file system this node instance represents
	 */
	public Path getPath() {
		return this.path;
	}

	@Override
	public @Nullable FsGroupingNode getParent() {
		return this.parent;
	}

	@Override
	public Optional<List<FsGroupingNode>> getChildren() {
		if (!Files.isDirectory(this.path)) {
			return Optionals.empty();
		}

		try (@SuppressWarnings("null")
		final @NonNull DirectoryStream<Path> directoryPaths = Files.newDirectoryStream(this.path)) {
			return Optionals.of(createChildren(directoryPaths));
		} catch (final IOException ioException) {
			throw new UncheckedIOException(ioException);
		}
	}

	protected List<FsGroupingNode> createChildren(final Iterable<Path> directoryPaths) {
		final var result = new ArrayList<FsGroupingNode>();
		for (final Path path : directoryPaths) {
			result.add(new FsGroupingNode(path, this));
		}

		return result;
	}
}
