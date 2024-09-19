package org.codeturnery.tree;

import java.util.List;

/**
 * A merge based on nodes within another merge.
 * 
 * @param <N> the type of nodes merged
 */
public class NodeSubMerge<N extends ChildableNode<N>> extends NodeMerge<N> {
	/**
	 * The non-leaf nodes from whose children this instance was created.
	 */
	private final List<N> parents;

	NodeSubMerge(final List<N> leaves, final List<NodeSubMerge<N>> submerges, final List<N> parents) {
		super(leaves, submerges);
		this.parents = parents;
	}

	/**
	 * @return nodes within the parent merge from which this merge was created
	 */
	public List<N> getParents() {
		return this.parents;
	}
}
