package org.codeturnery.tree;

import java.util.List;
import java.util.stream.Stream;

/**
 * Represents multiple nodes merged together.
 *
 * @param <N> the type of the nodes merged together
 */
public class NodeMerge<N extends ChildableNode<N>> {
	/**
	 * Nodes that were merged by this instance and are leaves (i.e. can't have
	 * children themselves).
	 */
	private final List<N> leaves;
	/**
	 * The submerges of this instance.
	 */
	private final List<NodeSubMerge<N>> submerges;

	public NodeMerge(final List<N> leaves, final List<NodeSubMerge<N>> submerges) {
		this.leaves = leaves;
		this.submerges = submerges;
	}

	public Stream<N> getMergedNodesStream() {
		return Stream.concat(getLeaves().stream(), getNonLeavesStream());
	}

	public List<N> getLeaves() {
		return this.leaves;
	}

	public List<NodeSubMerge<N>> getSubmerges() {
		return this.submerges;
	}

	public Stream<N> getNonLeavesStream() {
		return this.submerges.stream().map(NodeSubMerge::getParents).flatMap(List::stream).distinct();
	}
}
