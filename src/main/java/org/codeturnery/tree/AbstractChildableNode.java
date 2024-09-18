package org.codeturnery.tree;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.codeturnery.typesystem.Iterables;

/**
 * @param <N> the type of the child nodes
 */
abstract public class AbstractChildableNode<N extends ChildableNode<N>> implements ChildableNode<N>, Comparable<ChildableNode<N>>{
	@Override
	public Optional<Stream<N>> getChildrenStream() {
		return getChildren().map(List::stream);
	}

	@Override
	public Optional<Stream<N>> getChildrenStream(final Predicate<N> filter) {
		return getChildrenStream().map(stream -> stream.filter(filter));
	}

	@Override
	public Optional<Stream<ChildFilteringNode<N>>> getChildren(final Predicate<N> filter, final boolean recursive) {
		return new ChildFilteringNode<>(this, filter, recursive).getChildrenStream();
	}
	
	@Override
	public int compareTo(final ChildableNode<N> other) {
		return getChildrenCount() - other.getChildrenCount();
	}
	
	@Override
	public int getChildrenCount() {
		return Math.toIntExact(getChildrenStream().orElse(Iterables.emptyStream()).count());
	}
}
