package org.codeturnery.tree;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @param <N> the type of the original base node and its children
 */
public class ChildFilteringNode<N extends ChildableNode<N>> implements ChildableNode<ChildFilteringNode<N>> {
	private final ChildableNode<N> baseNode;
	private final Predicate<N> filter;
	private final boolean recursive;

	ChildFilteringNode(final ChildableNode<N> childableNode, final Predicate<N> filter, final boolean recursive) {
		this.baseNode = childableNode;
		this.filter = filter;
		this.recursive = recursive;
	}

	@Override
	public Optional<Stream<ChildFilteringNode<N>>> getChildrenStream() {
		final Optional<Stream<N>> flatFilteredNodes = this.baseNode.getChildrenStream()
				.map(stream -> stream.filter(this.filter));

		return flatFilteredNodes.map(stream -> stream.map(
				child -> new ChildFilteringNode<>(child, this.recursive ? this.filter : x -> true, this.recursive)));
	}

	@Override
	public Optional<Stream<ChildFilteringNode<N>>> getChildrenStream(final Predicate<ChildFilteringNode<N>> filter) {
		return getChildrenStream().map(stream -> stream.filter(filter));
	}

	@Override
	public Optional<Stream<ChildFilteringNode<ChildFilteringNode<N>>>> getChildren(
			final Predicate<ChildFilteringNode<N>> filter, final boolean recursive) {
		return new ChildFilteringNode<>(this, filter, recursive).getChildrenStream();
	}

	public ChildableNode<N> getBaseNode() {
		return this.baseNode;
	}

	@Override
	public Optional<List<ChildFilteringNode<N>>> getChildren() {
		return this.getChildrenStream().map(Stream::toList);
	}
}
