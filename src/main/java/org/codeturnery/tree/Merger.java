package org.codeturnery.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

/**
 * Provides logic to merge nodes into instances representing them.
 * 
 * @param <N> the type of the merged nodes
 */
public class Merger<N extends ChildableNode<N>> {
	private final Hasher<N> hasher;

	/**
	 * Creates an instance based on the provided hasher.
	 * 
	 * @param hasher provides the logic to create a hash for a given node
	 */
	public Merger(final Hasher<N> hasher) {
		this.hasher = hasher;
	}

	/**
	 * @param nodes the nodes to be group by their {@link #hasher hash}, with a
	 *              {@link NodeMerge} created for each hash value.
	 * @return {@link NodeMerge} instances, in total containing all the given nodes
	 *         without a node being present in multiple instances.
	 */
	public Stream<NodeMerge<N>> separateAndCreateMerges(final List<N> nodes) {
		final Map<Integer, List<N>> separatedNodes = groupByHashes(nodes);
		return separatedNodes.values().stream().map(list -> {
			final Pair<List<N>, Stream<NodeSubMerge<N>>> leavesAndSubmerges = mapIntoLeavesAndSubMerges(list);
			return new NodeMerge<>(leavesAndSubmerges.getX(), leavesAndSubmerges.getY().toList());
		});
	}

	/**
	 * 
	 * @param nodes   The nodes to be merged.
	 * @param parents the parents of the given nodes
	 * @return A merge created from the given nodes, also storing the parents of the
	 *         given nodes for later usage.
	 */
	protected NodeSubMerge<N> createSubMerge(final Collection<N> nodes, final List<N> parents) {
		final var leavesAndSubMerges = mapIntoLeavesAndSubMerges(nodes);
		return new NodeSubMerge<>(leavesAndSubMerges.getX(), leavesAndSubMerges.getY().toList(), parents);
	}

	protected Pair<List<N>, Stream<NodeSubMerge<N>>> mapIntoLeavesAndSubMerges(final Collection<N> nodes) {
		// the given leaf nodes
		final var leaves = new ArrayList<N>();

		// Mapping from a hash to the corresponding children retrieved from a non-leaf
		// node. The children are stored as pairs containing their parent too.
		final var nonLeaves = new HashMap<Integer, List<Pair<N, List<N>>>>();

		// fill the leaves and nonLeaves variables above
		for (final N node : nodes) {
			node.getChildren().ifPresentOrElse(children -> {
				// in case of a non-leaf, group its children by their hashes
				final Map<Integer, List<N>> groupedChildren = groupByHashes(children);
				// convert the structure and fill the nonLeaves variable
				for (final Entry<Integer, List<N>> childGroup : groupedChildren.entrySet()) {
					final Integer hash = childGroup.getKey();
					final List<Pair<N, List<N>>> parentsWithChildren = nonLeaves.computeIfAbsent(hash,
							key -> new ArrayList<>());
					parentsWithChildren.add(new Pair<>(node, childGroup.getValue()));
				}
			}, () -> {
				// in case of a leaf, simply add it to the other ones
				leaves.add(node);
			});
		}

		// the pairs grouped by the hashes are converted into NodeMerge instances
		final Stream<NodeSubMerge<N>> subMerges = createSubMerges(nonLeaves.values().stream());

		return new Pair<>(leaves, subMerges);
	}

	protected Stream<NodeSubMerge<N>> createSubMerges(final Stream<List<Pair<N, List<N>>>> nonLeaves) {
		return nonLeaves.map(pairsToMerge -> {
			final var mergedParents = new ArrayList<N>();
			final var mergedChildren = new ArrayList<N>();
			for (final Pair<N, List<N>> pair : pairsToMerge) {
				mergedParents.add(pair.getX());
				mergedChildren.addAll(pair.getY());
			}
			// a NodeMerge instance, contains the parents from which it was created
			// this is done recursively, as the called method will call this method again
			return createSubMerge(mergedChildren, mergedParents);
		});
	}

	/**
	 * Calculates the hashes of the given nodes and groups them by their hashes.
	 * 
	 * @param nodes The nodes to group by their hashes.
	 * @return Mapping from an hash to the nodes for which this hash was calculated.
	 */
	protected Map<Integer, List<N>> groupByHashes(final Iterable<N> nodes) {
		final var groupedNodes = new HashMap<Integer, List<N>>();
		for (final N node : nodes) {
			final int hash = this.hasher.getHash(node);
			final List<N> group = groupedNodes.computeIfAbsent(hash, key -> new ArrayList<>());
			group.add(node);
		}

		return groupedNodes;
	}
}
