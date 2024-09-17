package org.codeturnery.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @param <N> the type of the nodes
 */
public class StatisticsCalculator<N extends ChildableNode<N>> {
	/**
	 * Hasher containing
	 */
	private final Grouper<N> grouper;

	public StatisticsCalculator(final Grouper<N> grouper) {
		this.grouper = grouper;
	}

	/**
	 * Calculates and returns some statistical information for each group of
	 * children found inside this merge.
	 * <p>
	 * For each given non-leaf node it's children are grouped according to
	 * predicates set in {@link #grouper}. I.e. each node may result in many groups,
	 * identified by the predicate that determined which children of that node
	 * should be placed in which group. This means that <code>n</code> nodes will
	 * result in <code>n</code> sets of groups, where a specific predicate may be
	 * present in each set.
	 * <p>
	 * Each returned statistic will consider the information for all nodes. E.g. if
	 * two nodes were given, the first one containing two children and the second
	 * one containing four children, and all those children match the same
	 * predicate, then the corresponding, created statistic would contain the
	 * information that:
	 * 
	 * <ul>
	 * <li>in total six children matched the predicate</li>
	 * <li>each of the two nodes contained at least two children belonging to that
	 * predicate</li>
	 * <li>each of the two nodes contained at most four children belonging to that
	 * predicate</li>
	 * <li>for all nodes in which the predicate matched any children, the average of
	 * children matching the predicate is three</li>
	 * </ul>
	 * 
	 * @param nonLeavesOfMerge the given nodes, of which the children are matched
	 *                         against predicates
	 * @return the mapping from a predicate that matched children in the given nodes
	 *         to a statistic that gives information about the matching count in the
	 *         different nodes
	 */
	public Map<Predicate<N>, GroupingStats> getStats(final List<N> nonLeavesOfMerge) {
		final var result = new HashMap<Predicate<N>, GroupingStats>();
		for (int i = 0; i < nonLeavesOfMerge.size(); i++) {
			final var parent = nonLeavesOfMerge.get(i);
			final Optional<List<Group<N>>> groups = this.grouper.getGroups(parent);
			groups.ifPresent(groupList -> {
				for (final Group<N> group : groupList) {
					final Predicate<N> predicate = group.getPredicate();
					final int nodeCount = group.getNodes().size();
					final GroupingStats stats = result.computeIfAbsent(predicate, key -> new GroupingStats(nodeCount));
					stats.addGroupChildCount(nodeCount);
				}
			});
		}

		return result;
	}
}
