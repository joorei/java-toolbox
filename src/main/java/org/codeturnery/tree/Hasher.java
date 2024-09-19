package org.codeturnery.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.codeturnery.typesystem.Iterables;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Calculates hashes for nodes and stores them for later retrieval to avoid
 * re-calculation.
 * 
 * @param <N> the type of the nodes and child nodes hashed by this instance
 */
abstract public class Hasher<N extends ChildableNode<N>> {
	/**
	 * Cached mapping from a hash to corresponding nodes (i.e. nodes for which this
	 * hash was calculated).
	 */
	private final Map<Integer, List<ChildableNode<N>>> hashToNodeMapping = new HashMap<>();
	/**
	 * Cached mapping from a node to its hash.
	 */
	private final Map<ChildableNode<N>, Integer> nodeToHashMapping = new HashMap<>();
	/**
	 * Cached mapping from a hash to the corresponding groups.
	 */
	private final Map<Integer, List<Group<N>>> hashToGroupMapping = new HashMap<>();
	/**
	 * Cached mapping from a group to its hash.
	 */
	private final Map<Group<N>, Integer> groupToHashMapping = new HashMap<>();

	/**
	 * The instance to calculate groups from nodes with.
	 */
	protected final Grouper<N> grouper;

	/**
	 * @param grouper The instance to calculate groups from nodes with.
	 */
	public Hasher(final Grouper<N> grouper) {
		this.grouper = grouper;
	}

	/**
	 * Get the mappings from a hash to the corresponding nodes. Only nodes for which
	 * the hash was previously retrieved from this instance will be present.
	 * 
	 * @param predicates the conditions that all returned entry must fulfill
	 * @return the mapping from hashes to corresponding nodes that match the given
	 *         conditions
	 */
	public Map<Integer, List<ChildableNode<N>>> getHashToNodeMapping(
			final Set<Predicate<Entry<Integer, List<ChildableNode<N>>>>> predicates) {
		Stream<Entry<Integer, List<ChildableNode<N>>>> stream = this.hashToNodeMapping.entrySet().stream();
		for (final Predicate<Entry<Integer, List<ChildableNode<N>>>> predicate : predicates) {
			stream = stream.filter(predicate);
		}
		return stream.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	/**
	 * Get all nodes with the given hash currently known to this instance.
	 * <p>
	 * For a node to be known, its hash must have been retrieved from this instance
	 * previously.
	 * <p>
	 * A node is known to correspond to a hash, if it was previously calculated by
	 * calling the {@link #getHash} method.
	 * 
	 * @param hash the hash the returned nodes match
	 * @return the nodes stored in this instance so far, that match the given hash
	 */
	public List<ChildableNode<N>> getNodes(final int hash) {
		return this.hashToNodeMapping.getOrDefault(hash, Iterables.emptyList());
	}

	/**
	 * Get all groups with the given hash currently known to this instance.
	 * <p>
	 * For a group to be known, its hash must have been retrieved from this instance
	 * previously.
	 * 
	 * @param hash the hash for which corresponding groups are to be returned
	 * @return the groups corresponding to a hash
	 */
	public List<Group<N>> getGroups(final int hash) {
		return this.hashToGroupMapping.getOrDefault(hash, Iterables.emptyList());
	}

	/**
	 * Returns the hash for the given node.
	 * <p>
	 * If the hash for the given node was calculated previously by this instance, it
	 * was stored and can be returned immediately. Otherwise it will be calculated
	 * and stored for later retrievals.
	 * 
	 * @param node the node to calculate the hash for
	 * @return the hash for the given node, how it was calculated depends on the
	 *         implementation of this method
	 */
	public int getHash(final ChildableNode<N> node) {
		final @Nullable Integer maybeHash = this.nodeToHashMapping.get(node);
		if (maybeHash == null) {
			// do not calculate the hash within computeIfAbsend, to avoid concurrent map
			// accesses
			final int hash = calculateHashFromGroupHashes(node);
			return this.nodeToHashMapping.computeIfAbsent(node, key -> hash);
		}
		return maybeHash;

	}

	/**
	 * Get the hash of the given group. Will be calculated and cached if this is the
	 * first time it is requested.
	 * 
	 * @param group the group to retrieve the hash for
	 * @return the hash of the given group
	 */
	public int getHash(final Group<N> group) {
		return this.groupToHashMapping.computeIfAbsent(group, this::calculateHash);
	}

	/**
	 * Calculate the hash of the given node.
	 * <p>
	 * Will calculate the individual hashes of the node's groups using
	 * {@link Hasher#calculateHash(Group)} and summarize them to the hash of the
	 * node.
	 * <p>
	 * This approach assumes that two nodes will only be equal if their individual
	 * groups are equal. This will not cover some cases, like considering two nodes
	 * to be equal if they simply contain at least three groups.
	 * <p>
	 * The node will be added to {@link #hashToNodeMapping}, but
	 * <strong>not</strong> {@link #nodeToHashMapping}.
	 * 
	 * @param node the node to calculate the hash for
	 * @return the hash calculated for the given node
	 */
	protected int calculateHashFromGroupHashes(final ChildableNode<N> node) {
		final List<Group<N>> groups = this.grouper.getGroups(node)
				.orElse(Iterables.emptyList()/* TODO: how to handle correctly? */);
		final var groupHashes = new int[groups.size()];
		int groupIndex = 0;
		for (final Group<N> group : groups) {
			groupHashes[groupIndex++] = calculateHash(group);
		}
		final int nodeHash = Arrays.hashCode(groupHashes);
		final List<ChildableNode<N>> sameHashNodes = this.hashToNodeMapping.computeIfAbsent(nodeHash,
				hash -> new ArrayList<>());
		sameHashNodes.add(node);

		return nodeHash;
	}

	/**
	 * Calculate the hash for a single group within a node.
	 *
	 * @param group the group to calculate the hash for
	 * @return the hash calculated for the given group
	 */
	abstract protected int calculateHash(final Group<N> group);
}
