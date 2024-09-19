package org.codeturnery.tree;

/**
 * Represents information for children of multiple nodes, that matched the same
 * predicate.
 */
public class GroupingStats {
	/**
	 * No group contained less than this amount of children.
	 */
	private int minimalCount;

	/**
	 * No group contained more than this amount of children.
	 */
	private int maximalCount;

	/**
	 * The sum of all children in all groups.
	 */
	private int childSum = 0;

	/**
	 * The number of groups.
	 */
	private int groupCount = 0;

	GroupingStats(final int firstGroupChildCount) {
		this.minimalCount = firstGroupChildCount;
		this.maximalCount = firstGroupChildCount;
	}

	/**
	 * From the group information that were added from different merges, this is the
	 * number of items in the group with the least number of items.
	 * 
	 * @return the number of items in the smallest group
	 */
	public int getMinimalCount() {
		return this.minimalCount;
	}

	/**
	 * From the group information that were added from different merges, this is the
	 * number of items in the group with the most number of items.
	 * 
	 * @return the number of items in the biggest group
	 */
	public int getMaximalCount() {
		return this.maximalCount;
	}

	/**
	 * @return Arithmetic Mean.
	 */
	public float getAverage() {
		return this.childSum / (float) this.groupCount;
	}

	/**
	 * Add the child count of a group of a merge.
	 * <p>
	 * Increases the number of groups from merges by one and adds the node count to
	 * the sum of all items from previously added groups. If this group has less or
	 * more items than any of the previously added groups, the value will be set as
	 * new minimum/maximum.
	 * 
	 * @param nodeCount the number of items in a group of a merge
	 */
	public void addGroupChildCount(int nodeCount) {
		if (nodeCount > this.maximalCount) {
			this.maximalCount = nodeCount;
		}
		if (nodeCount < this.minimalCount) {
			this.minimalCount = nodeCount;
		}

		this.childSum += nodeCount;
		this.groupCount += 1;
	}

	/**
	 * @return the sum of all items in all previously added groups
	 */
	public int getChildSum() {
		return this.childSum;
	}

	/**
	 * @return the number of groups that were added to this instance from different
	 *         merges
	 */
	public int getGroupCount() {
		return this.groupCount;
	}
}
