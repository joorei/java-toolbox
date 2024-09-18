package org.codeturnery.tree;

import java.util.List;
import java.util.function.Predicate;

public class Group<N> implements Comparable<Group<N>> {
	private final Predicate<N> predicate;
	private final List<N> nodes;

	public Group(final Predicate<N> predicate, final List<N> nodes) {
		this.predicate = predicate;
		this.nodes = nodes;
	}

	public Predicate<N> getPredicate() {
		return this.predicate;
	}

	public List<N> getNodes() {
		return this.nodes;
	}

	@Override
	public int compareTo(final Group<N> other) {
		return this.nodes.size() - other.nodes.size();
	}
}
