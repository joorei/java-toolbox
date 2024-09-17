package org.codeturnery.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Allows to separate a node's children different groups, based on provided
 * predicates.
 * <p>
 * Note this stage of grouping does not necessarily determine how nodes are
 * merged at a later point. Consider the following example: you have two nodes,
 * representing a directories each. Both directories contain images and movies.
 * You could group their content accordingly and the result would be the two
 * nodes containing two groups each:
 * 
 * <pre>
 * node A (directory A)
 *   group A1 (images)
 *   group A2 (movies)
 * node B (directory B)
 *   group B1 (images)
 *   group B2 (movies)
 * </pre>
 * 
 * However, when attempting to merge the two directories you may want to
 * consider group <code>A1</code> equal to <code>B1</code> only if they contain
 * the same number of images.
 * 
 * @param <N> the type of the nodes
 */
public class Grouper<N extends ChildableNode<N>> {
	private final Iterable<Predicate<N>> predicates;

	private final Map<ChildableNode<N>, Optional<List<Group<N>>>> nodeToGroupsMapping = new HashMap<>();

	public Grouper(final Iterable<Predicate<N>> predicates) {
		this.predicates = predicates;
	}

	/**
	 * Get the grouped children for a specific node.
	 * 
	 * @param parent the node whose children are grouped
	 * @return the grouped children or an empty optional, if the given node can't
	 *         have children
	 */
	public Optional<List<Group<N>>> getGroups(final ChildableNode<N> parent) {
		return this.nodeToGroupsMapping.computeIfAbsent(parent, key -> getGroupsForChildren(key).map(Stream::toList));
	}

	/**
	 * Group the children of the given node according to the set
	 * {@link #predicates}.
	 * <p>
	 * A child will be associated with the first matching predicate. The order of
	 * children within their group is preserved.
	 * 
	 * @param parent the node whose children shall be grouped
	 * 
	 * @return An empty {@link Optional} if this node is not of a type that could
	 *         have children. E.g. in a simple context of files and directories a
	 *         directory may return an empty {@link List}, but not an empty
	 *         {@link Optional}, whereas a file would return an empty
	 *         {@link Optional}.
	 */
	public Optional<Stream<Group<N>>> getGroupsForChildren(final ChildableNode<N> parent) {
		return parent.getChildrenStream().map(this::getGroupsForNodes);
	}

	/**
	 * Use the given predicates to group the given nodes.
	 * <p>
	 * This method is non-recursive, i.e. it will group the given children only and
	 * <strong>not</strong> step into them to discover potential grand children.
	 * <p>
	 * Each returned group contains a different predicate.
	 * 
	 * @param nodes the nodes to be grouped according to the set {@link #predicates}
	 * @return mapping from the first group matcher matching one of the given
	 *         children to all matching children; order of the children is preserved
	 */
	public Stream<Group<N>> getGroupsForNodes(final Stream<N> nodes) {
		final var preGrouping = new HashMap<Predicate<N>, List<N>>();
		// iterate through each child
		nodes.forEach(node -> {
			// sort the current child into the first matching group
			for (final Predicate<N> matcher : this.predicates) {
				// this matching step to put the child _into_ a group must not be confused with
				// the hash calculation _for_ a (filled) group, which does not happen here
				if (matcher.test(node)) {
					final List<N> nodesInGroup = preGrouping.computeIfAbsent(matcher, key -> new ArrayList<>());
					nodesInGroup.add(node);
					break;
				}
			}
		});

		return preGrouping.entrySet().stream()
				.map(groupEntry -> new Group<>(groupEntry.getKey(), groupEntry.getValue()));
	}
}
