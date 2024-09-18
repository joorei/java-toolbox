package org.codeturnery.tree;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNull;

/**
 * Represents multiple nodes merged together.
 *
 * @param <N> the type of the nodes merged together
 */
public class NodeMerge<N extends ChildableNode<N>> implements Comparable<NodeMerge<N>> {
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

	@Override
	public int compareTo(final NodeMerge<N> other) {
		// the raw node count merged by this instance has the highest priority
		final int nodesCount = getMergedNodesCount() - other.getMergedNodesCount();
		if (nodesCount != 0) {
			return nodesCount;
		}
		// if the raw node count is the same, the number of submerges created from those
		// nodes has the next priority
		final int submergeCount = getSubMergeCount() - other.getSubMergeCount();
		if (submergeCount != 0) {
			return submergeCount;
		}
		// if the number of submerges is the same too, the number of non-leaf nodes is
		// considered next (which is more simple to check via the inversed number of
		// leaf nodes)
		return other.getLeavesCount() - getLeavesCount();
	}

	public int getLeavesCount() {
		return getLeaves().size();
	}

	public int getSubMergeCount() {
		return getSubmerges().size();
	}

	public int getMergedNodesCount() {
		return Math.toIntExact(getMergedNodesStream().count());
	}
}
