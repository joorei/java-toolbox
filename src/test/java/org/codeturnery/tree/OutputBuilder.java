package org.codeturnery.tree;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * TODO: add configuration, allowing to check file content if it is an image
 * (using library)
 * 
 * TODO: add functionalities: * cases in which only one item was found (e.g. "1
 * × Folders, containing") should be made more helping by noting the exact name
 * * "at least 2 × Images" should be made more helpful, e.g. by naming the
 * minimum, maximum and additional values based on provided logic (e.g. average)
 * 
 * Especially the second functionality requires each file to be remembered until
 * a directory is done, though it may still be possible to not have to keep all
 * files in memory until they are outputted.
 * 
 * Additionally: there is a selection implementation needed where a user can
 * select individual directories to be imported, thus the preview needs to
 * display a summarization of each visible directory, so that opening
 * (expanding) it becomes optional
 */
public class OutputBuilder {

	/**
	 * Mapping from a predicate reference from which groups were created to the name
	 * to display for that group.
	 */
	private final Map<Predicate<TestNode>, String> predicateNaming;
	/**
	 * Instance to create group statistics.
	 */
	private final StatisticsCalculator<TestNode> statsCalculator;
	/**
	 * Instance to group nodes.
	 */
	private final Grouper<TestNode> grouper;
	/**
	 * The builder filled by this instance.
	 */
	private final StringBuilder builder = new StringBuilder();

	private final char indentationChar = ' ';
	private final int indentationMultipler = 2;

	/**
	 * Create a new instance, using the given predicate naming and instances for
	 * statistics and group calculation.
	 * 
	 * @param predicateNaming Mapping from a predicate reference from which groups
	 *                        were created to the name to display for that group.
	 * @param statsCalculator Instance to create group statistics.
	 * @param grouper         Instance to group nodes.
	 */
	public OutputBuilder(final Map<Predicate<TestNode>, String> predicateNaming,
			final StatisticsCalculator<TestNode> statsCalculator, final Grouper<TestNode> grouper) {
		this.predicateNaming = predicateNaming;
		this.grouper = grouper;
		this.statsCalculator = statsCalculator;
	}

	/**
	 * Adds the information from the given merge as strings to the string builder.
	 * 
	 * @param merge the instance to add
	 */
	public void addMerge(final NodeMerge<TestNode> merge) {
		addMerge(merge, 0, getMergeName(merge, 1, 1, 0));
		this.builder.append('\n');
	}

	protected String getMergeName(final NodeMerge<TestNode> merge, final int index, final int count, final int depth) {
		return "M" + depth + '-' + index + '/' + count; // Integer.toHexString(merge.hashCode());
	}

	/**
	 * Adds the information from the given merge as strings to the string builder.
	 * For each line the given indentation is used.
	 * 
	 * @param merge       the instance to add
	 * @param indentation the number of spaces to use as indentation
	 * @param mergeName   the name to present the merge as
	 */
	protected void addMerge(final NodeMerge<TestNode> merge, int indentation, final String mergeName) {
		fillWithChar(indentation);
		this.builder.append("• ");
		this.builder.append(mergeName);
		final Stream<Group<TestNode>> groups = this.grouper.getGroupsForNodes(merge.getMergedNodesStream());
		addMergeGrouping(groups);

		final List<TestNode> nonLeavesOfMerge = merge.getNonLeavesStream().toList();
		if (nonLeavesOfMerge.size() > 0) {
			final Map<Predicate<TestNode>, GroupingStats> stats = this.statsCalculator.getStats(nonLeavesOfMerge);
			addStats(stats);
			this.builder.append("; ");
			this.builder.append(merge.getSubmerges().stream().flatMap(m -> m.getMergedNodesStream()).count());
			this.builder.append(" children in the ");
			this.builder.append(nonLeavesOfMerge.size());
			this.builder.append(" non-leaf nodes were merged as follows:");
		}
		final List<NodeSubMerge<TestNode>> submerges = merge.getSubmerges();
		final int submergeCount = submerges.size();
		indentation += 1;
		for (int i = 0; i < submergeCount; i++) {
			this.builder.append('\n');
			final NodeSubMerge<TestNode> submerge = submerges.get(i);
			addMerge(submerge, indentation, getMergeName(submerge, i, submergeCount, indentation));
		}
	}

	/**
	 * Adds the given groups to the string builder.
	 * 
	 * @param groups The groups to add.
	 */
	protected void addMergeGrouping(final Stream<Group<TestNode>> groups) {
		groups.forEach(group -> {
			this.builder.append(", ");
			this.builder.append(group.getNodes().size());
			this.builder.append('×');
			this.builder.append(this.predicateNaming.get(group.getPredicate()));
		});
	}

	/**
	 * Adds the given group statistics to the string builder.
	 * 
	 * @param stats the statistics to be added
	 */
	protected void addStats(final Map<Predicate<TestNode>, GroupingStats> stats) {
		this.builder.append(" | Stats for children:");
		for (final Entry<Predicate<TestNode>, GroupingStats> statEntry : stats.entrySet()) {
			final GroupingStats stat = statEntry.getValue();
			final int min = stat.getMinimalCount();
			final int max = stat.getMaximalCount();
			this.builder.append(" (");
			this.builder.append(this.predicateNaming.get(statEntry.getKey()));
			this.builder.append(":");
			addClosedInterval(min, max);
			if (min != max) {
				this.builder.append(",");
				addAverage(stat);
			}
			this.builder.append(")");
		}
	}

	/**
	 * Adds a closed interval to the string builder.
	 * 
	 * @param min minimum value in the interval
	 * @param max maximum value in the interval
	 */
	protected void addClosedInterval(final int min, final int max) {
		this.builder.append('⟦');
		this.builder.append(min);
		this.builder.append(',');
		this.builder.append(max);
		this.builder.append('⟧');
	}

	/**
	 * @param stat the statistic to print
	 */
	protected void addAverage(final GroupingStats stat) {
		this.builder.append("x̄=");
		this.builder.append(stat.getChildSum());
		this.builder.append('÷');
		this.builder.append(stat.getGroupCount());
		this.builder.append('=');
		this.builder.append(stat.getAverage());
	}

	/**
	 * Adds the given character multiple times to the string builder.
	 * 
	 * @param count how many times the character should be added
	 */
	protected void fillWithChar(int count) {
		if (count > 0) {
			char[] array = new char[count * this.indentationMultipler];
			Arrays.fill(array, this.indentationChar);
			this.builder.append(array);
		}
	}

	/**
	 * Retrieves the current content of the string builder as {@link String}.
	 * <p>
	 * The {@link #builder} will not be reset or cleared.
	 * 
	 * @return the build string
	 */
	@SuppressWarnings("null")
	public String build() {
		return this.builder.toString();
	}
}
