package org.codeturnery.tree;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Uses the {@link Predicate} hash code of the predicate determining groups as
 * hash. I.e. groups are considered equal if they were created from the same
 * predicate.
 * <p>
 * If you assign a predicate to simply match every directory, this means that
 * directory content will be ignored for the hash calculation.
 * <p>
 * Alternatively, you can set <code>recursive</code> to <code>true</code>,
 * resulting in all subnodes being grouped and hashed too, with their hash being
 * used to calculate parent hashes.
 * 
 * @param <N> the type of the nodes and child nodes hashed by this instance
 */
public class GroupPredicateHasher<N extends ChildableNode<N>> extends Hasher<N> {
	private final Map<Predicate<N>, HashApproach> hashApproaches;

	public GroupPredicateHasher(final Grouper<N> grouper, final Map<Predicate<N>, HashApproach> hashApproaches) {
		super(grouper);
		this.hashApproaches = hashApproaches;
	}

	@Override
	protected int calculateHash(final Group<N> group) {
		final Predicate<N> groupPredicate = group.getPredicate();
		final HashApproach hashApproach = this.hashApproaches.getOrDefault(groupPredicate, HashApproach.EXACT_COUNT);
		final int predicateHashCode = groupPredicate.hashCode();
		if (HashApproach.PREDICATE_ONLY.equals(hashApproach)) {
			return predicateHashCode;
		}

		final var subgroupHashes = group.getNodes().stream().mapToInt(this::getHash).sorted().toArray();// ->
																										// this.grouper.getGroupsForChildren(node)).filter(groups
																										// ->
																										// groups.isPresent()).map(Optional::get);
		if (subgroupHashes.length == 0) {
			return predicateHashCode;
		}

		if (HashApproach.GROUP_EXISTENCE.equals(hashApproach)) {
			final int[] distinctSubgroupHashes = Arrays.stream(subgroupHashes).distinct().toArray();
			return Arrays.hashCode(new int[] { predicateHashCode, Arrays.hashCode(distinctSubgroupHashes) });
		}

		if (HashApproach.DIFFERENCIATE_NONE_ONE_MULTIPLE.equals(hashApproach)) {
			final int[] distinctSubgroupHashes = reduceDuplicatesTo(subgroupHashes, 2);
			return Arrays.hashCode(new int[] { predicateHashCode, Arrays.hashCode(distinctSubgroupHashes) });
		}

		if (HashApproach.EXACT_COUNT.equals(hashApproach)) {
			return Arrays.hashCode(new int[] { predicateHashCode, Arrays.hashCode(subgroupHashes) });
		}

		throw new IllegalArgumentException(hashApproach.name());
	}

	protected int[] reduceDuplicatesTo(final int[] hashes, final int duplicateMax) {
		int newLength = 0;
		int currentHashCount = 0;
		// TODO: we expect the hashes to come in sorted, so having the first hash
		// matching the max int value is quite unlikely; still, this doesn't seem ideal
		// as conflicts are still theoretically possible
		int lastHash = Integer.MAX_VALUE;
		for (int i = 0; i < hashes.length; i++) {
			final int currentHash = hashes[i];
			if (lastHash == currentHash) {
				currentHashCount++;
			} else {
				currentHashCount = 1;
			}
			if (currentHashCount <= duplicateMax) {
				newLength++;
			}
			lastHash = currentHash;
		}
		currentHashCount = 0;
		lastHash = Integer.MAX_VALUE;
		int newArrayIndex = 0;
		final var newArray = new int[newLength];
		for (int i = 0; i < hashes.length; i++) {
			final int currentHash = hashes[i];
			if (lastHash == currentHash) {
				currentHashCount++;
			} else {
				currentHashCount = 1;
			}
			if (currentHashCount <= duplicateMax) {
				newArray[newArrayIndex++] = currentHash;
			}
			lastHash = currentHash;
		}
		if (newArrayIndex != newLength) {
			throw new IllegalStateException(newArrayIndex + "|" + newLength);
		}

		return newArray;
	}
}
