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

	public GroupingStats(final int firstGroupChildCount) {
		this.minimalCount = firstGroupChildCount;
		this.maximalCount = firstGroupChildCount;
	}

	public int getMinimalCount() {
		return this.minimalCount;
	}

	public int getMaximalCount() {
		return this.maximalCount;
	}

	/**
	 * @return Arithmetic Mean.
	 */
	public float getAverage() {
		return this.childSum / (float) this.groupCount;
	}

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

	public int getChildSum() {
		return this.childSum;
	}

	public int getGroupCount() {
		return this.groupCount;
	}
}
