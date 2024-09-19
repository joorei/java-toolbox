package org.codeturnery.tree;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Provides a starting point to implement the conversion of {@link NodeMerge}
 * instances into a different structure, format or representation.
 * 
 * @param <N> the type of nodes merged
 */
abstract public class AbstractMergeOutputBuilder<N extends ChildableNode<N>> {

	/**
	 * Mapping from a predicate reference from which groups were created to the name
	 * to display for that group.
	 */
	private final Map<Predicate<N>, String> predicateNaming;
	/**
	 * Instance to create group statistics.
	 */
	private final StatisticsCalculator<N> statsCalculator;
	/**
	 * Instance to group nodes.
	 */
	private final Grouper<N> grouper;

	private int addedMerges = 0;

	/**
	 * Create a new instance, using the given predicate naming and instances for
	 * statistics and group calculation.
	 * 
	 * @param predicateNaming Mapping from a predicate reference from which groups
	 *                        were created to the name to display for that group.
	 * @param statsCalculator Instance to create group statistics.
	 * @param grouper         Instance to group nodes.
	 */
	public AbstractMergeOutputBuilder(final Map<Predicate<N>, String> predicateNaming,
			final StatisticsCalculator<N> statsCalculator, final Grouper<N> grouper) {
		this.predicateNaming = predicateNaming;
		this.grouper = grouper;
		this.statsCalculator = statsCalculator;
	}

	/**
	 * Adds the information from the given merge as strings to the string builder.
	 * 
	 * @param merge the instance to add
	 * @param count the amount of merges that will be added to this instance in
	 *              total
	 */
	public void addMerge(final NodeMerge<N> merge, final int count) {
		final String mergeName = getMergeName(merge, ++this.addedMerges, count, 0);
		addMerge(merge, 0, mergeName);
		this.addMergeSeparator();
	}

	/**
	 * Adds the information from the given merge as strings to the string builder.
	 * For each line the given indentation is used.
	 * 
	 * @param merge     the instance to add
	 * @param depth     the number of spaces to use as indentation
	 * @param mergeName the name to present the merge as
	 */
	protected void addMerge(final NodeMerge<N> merge, int depth, final String mergeName) {
		addMergeName(mergeName);
		final Stream<Group<N>> groups = this.grouper.getGroupsForNodes(merge.getMergedNodesStream()).sorted((a, b) -> {
			/*
			 * we use the order in the predicate naming to make the group smaller whose
			 * predicate is positioned in the naming map sooner. (note that the map is
			 * assumed to be ordered)
			 */
			for (final Predicate<?> predicateName : this.predicateNaming.keySet()) {
				if (predicateName.equals(a.getPredicate())) {
					return 1;
				}
				if (predicateName.equals(b.getPredicate())) {
					return -1;
				}
			}
			return 0;
		});
		addMergeGrouping(groups);

		final List<N> nonLeavesOfMerge = merge.getNonLeavesStream().sorted().toList();
		this.addMergeChildrenInfos(nonLeavesOfMerge.size(),
				merge.getSubmerges().stream().flatMap(m -> m.getMergedNodesStream()).count(),
				this.statsCalculator.getStats(nonLeavesOfMerge));
		final List<NodeSubMerge<N>> submerges = merge.getSubmerges().stream().sorted().toList();
		final int submergeCount = submerges.size();
		depth += 1;
		for (int i = 0; i < submergeCount; i++) {
			addMergeSeparator();
			final NodeSubMerge<N> submerge = submerges.get(i);
			final String submergeName = getMergeName(submerge, i + 1, submergeCount, depth);
			addMerge(submerge, depth, submergeName);
		}
	}

	/**
	 * Adds the given group statistics to the string builder.
	 * 
	 * @param stats the statistics to be added
	 */
	protected void addStats(final Map<Predicate<N>, GroupingStats> stats) {
		this.addChildrenStatsHeader();
		// iterating over the naming instead of the stats allows for a predictable order
		for (final Entry<Predicate<N>, String> predicateEntry : this.predicateNaming.entrySet()) {
			final Predicate<N> predicate = predicateEntry.getKey();
			if (!stats.containsKey(predicate)) {
				continue;
			}
			final GroupingStats stat = stats.get(predicate);
			final int min = stat.getMinimalCount();
			final int max = stat.getMaximalCount();
			this.addStat(predicateEntry.getValue(), min, max, stat);
		}
	}

	protected String getMergeName(final NodeMerge<N> merge, final int index, final int count, final int depth) {
		return "M" + depth + '-' + index + '/' + count; // Integer.toHexString(merge.hashCode());
	}

	protected void addMergeChildrenInfos(final long nonLeavesOfMergeCount, final long childCount,
			final Map<Predicate<N>, GroupingStats> stats) {
		if (nonLeavesOfMergeCount > 0) {
			addStats(stats);
			this.addInMergeDelimiter();
			addMergeChildInfo(childCount, nonLeavesOfMergeCount);
		}
	}

	protected abstract void addInMergeDelimiter();

	protected void addMergeGrouping(final Stream<Group<N>> groups) {
		groups.forEach(group -> {
			this.addMergeGroupDelimiter();
			addMergeGroup(group.getNodes().size(), this.predicateNaming.get(group.getPredicate()));
		});
	}

	protected abstract void addStat(final String predicateName, final int min, final int max, final GroupingStats stat);

	protected abstract void addChildrenStatsHeader();

	protected abstract void addAverage(int childSum, int groupCount, float average);

	protected abstract void addMergeGroup(int size, String string);

	protected abstract void addMergeGroupDelimiter();

	abstract protected void addData(final String dataAsString);

	abstract protected void addMergeName(String mergeName);

	abstract protected void addMergeSeparator();

	abstract protected void addMergeChildInfo(long childCount, long nonLeavesOfMergeCount);
}
