package org.codeturnery.tree;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;

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

		final int[] subgroupHashes = group.getNodes().stream().mapToInt(this::getHash).sorted().toArray();
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

	/**
	 * Create a new array containing the given hashes, but each distinct value being
	 * present a maximum amount of times.
	 * 
	 * @param hashes       the hashes to filter out values from, expected to be
	 *                     sorted from smallest to biggest value
	 * @param duplicateMax the maximum number of times a distinct hash value is
	 *                     allowed to be present in the output array
	 * @return the new array without the hashes filtered out, or the given array of
	 *         hashes, if nothing was filtered
	 */
	public int[] reduceDuplicatesTo(final int[] hashes, final int duplicateMax) {
		// "dummy" loop through the hashes to determine the needed array length
		final int countToStore = handleHashes(hashes, duplicateMax, null);

		// nothing was filtered, just return the input array to avoid initializations
		if (countToStore == hashes.length) {
			return hashes;
		}

		// with the loop above we found the exact length of the needed array
		final var newArray = new int[countToStore];

		// loop again the same way to actually store the hashes in the array
		final int usedArrayLength = handleHashes(hashes, duplicateMax, newArray);

		// sanity check: the loop above should have filled the new array completely
		if (countToStore != usedArrayLength) {
			throw new IllegalStateException(countToStore + "|" + usedArrayLength);
		}

		return newArray;
	}

	/**
	 * Iterates through the given array of hashes. The given consumer will be called
	 * for each item in the array, except if it was already called
	 * <code>duplicateMax</code> for that hash, in which case no call will be
	 * executed.
	 * 
	 * @param hashes               assumed to be sorted from smallest to biggest
	 *                             value
	 * @param maxCallCountPerValue the maximum number of times a distinct hash value
	 *                             is passed to <code>intConsumer</code>
	 * @param target               The target array into which the valid values
	 *                             should be placed into. Ignored if
	 *                             <code>null</code>. Must be large enough to hold
	 *                             all values.
	 * @return number valid values
	 */
	public int handleHashes(final int[] hashes, final int maxCallCountPerValue, final int @Nullable [] target) {
		int callCount = 0;
		int currentValueCount = 0;
		int previousValue = Integer.MAX_VALUE;
		for (int i = 0; i < hashes.length; i++) {
			final int currentValue = hashes[i];
			/*
			 * We note how often the current hash was already encountered by comparing it
			 * with the hash from the previous iteration. For the first iteration the value
			 * of previousHash does not matter; currentHashCount is always set to 1, either
			 * by incrementing it or explicitly setting it to 1.
			 */
			if (previousValue == currentValue) {
				currentValueCount++;
			} else {
				currentValueCount = 1;
			}
			/*
			 * Call the given consumer with the current hash, if it wasn't already called
			 * too many times with that distinct value.
			 */
			if (currentValueCount <= maxCallCountPerValue) {
				if (target != null) {
					if (target.length <= callCount) {
						throw new IllegalArgumentException(
								"Given target (" + target.length + ") not large enough to hold all values.");
					}
					target[callCount] = currentValue;
				}
				callCount++;
			}
			previousValue = currentValue;
		}

		return callCount;
	}
}
