package org.codeturnery.tree;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Predicate;

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
public class OutputBuilder extends AbstractMergeOutputBuilder<TestNode> {

	/**
	 * The builder filled by this instance.
	 */
	private final StringBuilder builder = new StringBuilder();

	private final char indentationChar = ' ';
	private final int indentationMultipler = 2;

	public OutputBuilder(final Map<Predicate<TestNode>, String> predicateNaming,
			final StatisticsCalculator<TestNode> statsCalculator, final Grouper<TestNode> grouper) {
		super(predicateNaming, statsCalculator, grouper);
	}

	@Override
	protected void addMerge(final NodeMerge<TestNode> merge, int depth, final String mergeName) {
		fillWithChar(depth);
		this.builder.append("• ");
		super.addMerge(merge, depth, mergeName);

	}

	@Override
	protected void addMergeName(final String mergeName) {
		this.builder.append(mergeName);
	}

	@Override
	protected void addInMergeDelimiter() {
		this.builder.append("; ");
	}

	@Override
	protected void addMergeChildInfo(final long childCount, final long nonLeavesOfMergeCount) {
		this.builder.append(childCount);
		this.builder.append(" children in the ");
		this.builder.append(nonLeavesOfMergeCount);
		this.builder.append(" non-leaf nodes were merged as follows:");
	}

	/**
	 * Adds the given group to the string builder.
	 */
	@Override
	protected void addMergeGroup(final int groupNodeCount, final String groupName) {
		this.builder.append(groupNodeCount);
		this.builder.append('×');
		this.builder.append(groupName);
	}

	@Override
	protected void addMergeGroupDelimiter() {
		this.builder.append(", ");
	}

	@Override
	protected void addChildrenStatsHeader() {
		this.builder.append(" | Stats for children:");
	}

	@Override
	protected void addStat(final String predicateName, final int min, final int max, final GroupingStats stat) {
		this.builder.append(" (");
		this.builder.append(predicateName);
		this.builder.append(":");
		addClosedInterval(min, max);
		if (min != max) {
			this.builder.append(",");
			this.addAverage(stat.getChildSum(), stat.getGroupCount(), stat.getAverage());
		}
		this.builder.append(")");
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

	@Override
	protected void addAverage(final int childSum, final int groupCount, final float average) {
		this.builder.append("x̄=");
		this.builder.append(childSum);
		this.builder.append('÷');
		this.builder.append(groupCount);
		this.builder.append('=');
		this.builder.append(average);
	}

	/**
	 * Adds the given character multiple times to the string builder.
	 * 
	 * @param count how many times the character should be added
	 */
	protected void fillWithChar(final int count) {
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

	@Override
	protected void addData(String dataAsString) {
		this.builder.append(dataAsString);
	}

	@Override
	protected void addMergeSeparator() {
		this.builder.append('\n');
	}
}
