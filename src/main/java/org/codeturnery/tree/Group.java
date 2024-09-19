package org.codeturnery.tree;

import java.util.List;
import java.util.function.Predicate;

/**
 * A group of nodes, matching a specific predicate.
 * 
 * @param <N> the type of nodes in this group
 */
public class Group<N> implements Comparable<Group<N>> {
	private final Predicate<N> predicate;
	private final List<N> nodes;

	Group(final Predicate<N> predicate, final List<N> nodes) {
		this.predicate = predicate;
		this.nodes = nodes;
	}

	/**
	 * @return The predicate a node must match to be sorted into this instance
	 */
	public Predicate<N> getPredicate() {
		return this.predicate;
	}

	/**
	 * @return the nodes sorted into this group
	 */
	public List<N> getNodes() {
		return this.nodes;
	}

	@Override
	public int compareTo(final Group<N> other) {
		return this.nodes.size() - other.nodes.size();
	}
}
