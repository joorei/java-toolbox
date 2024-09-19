package org.codeturnery.tree;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A node that may or may not be able to contain children and may or may not
 * actually contain any.
 * <p>
 * E.g. for nodes representing files and directories inside a (fully readable)
 * file system, for a node that specifically represents a file all methods would
 * return an empty {@link Optional}, as files (in this context) can't have
 * children. Nodes representing directories on the other hand would always
 * return a non-empty {@link Optional}, but if the directory is empty the
 * contained stream/list would be empty.
 * 
 * @param <N> the type of the child nodes
 */
public interface ChildableNode<N extends ChildableNode<N>> {
	/**
	 * Get all child nodes from this node.
	 * <p>
	 * The node hierarchy is preserved.
	 * 
	 * @return children from all groups
	 */
	public Optional<Stream<N>> getChildrenStream();

	/**
	 * Get child nodes from this node that match the given filter.
	 * <p>
	 * The node hierarchy is preserved. However, if a child node is removed by the
	 * filter, all of its children will no longer be part of the returned hierarchy
	 * as well.
	 * 
	 * @param filter the filter children must match to be returned
	 * @return children from all groups, with those removed not matching the
	 *         provided filter
	 */
	public Optional<Stream<N>> getChildrenStream(final Predicate<N> filter);

	/**
	 * Get child nodes from this node that match the given filter.
	 * <p>
	 * The node hierarchy is preserved. However, if a child node is removed by the
	 * filter, all of its children will no longer be part of the returned hierarchy
	 * as well.
	 * 
	 * @param filter    the filter children must match to be returned
	 * @param recursive if the filter should be applied to children of children (on
	 *                  all downward levels) as well
	 * @return children from all groups, with those removed not matching the
	 *         provided filter
	 */
	public Optional<Stream<ChildFilteringNode<N>>> getChildren(final Predicate<N> filter, final boolean recursive);

	/**
	 * The children of this node.
	 * 
	 * @return An empty {@link Optional} if this node is not a kind of node to have
	 *         children at all (e.g. a file in a directory). An {@link Optional}
	 *         containing an empty list if it could have children, but has none
	 *         (e.g. an empty directory).
	 */
	public Optional<List<N>> getChildren();

	/**
	 * @return number of children in this node; 0, if the node can't have any
	 *         children
	 */
	public int getChildrenCount();
}
