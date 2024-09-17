package org.codeturnery.tree;

import java.util.List;

public class NodeSubMerge<N extends ChildableNode<N>> extends NodeMerge<N> {
	/**
	 * The non-leaf nodes from whose children this instance was created.
	 */
	private final List<N> parents;

	public NodeSubMerge(final List<N> leaves, final List<NodeSubMerge<N>> submerges, final List<N> parents) {
		super(leaves, submerges);
		this.parents = parents;
	}

	public List<N> getParents() {
		return this.parents;
	}
}
