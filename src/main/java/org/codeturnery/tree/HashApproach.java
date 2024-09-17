package org.codeturnery.tree;

/**
 * When comparing groups there are different approaches available, especially in
 * regard how the number of items within a group should affect equality. What
 * groups are considered equal directly affects what directories are considered
 * equal.
 * <p>
 * To make this more tangible consider the following example directory
 * structure:
 * 
 * <pre>
 * <code>
 * dir1
 *   txt1
 *   txt2
 * dir2
 *   txt3
 * </code>
 * </pre>
 * 
 * Running the algorithm on this structure with different {@link HashApproach}es
 * for <code>txt</code> files has different effects on each run.
 * <p>
 * Example A: {@link #EXACT_COUNT}
 * <p>
 * <code>dir1</code> is set to contain a group of exactly two txt files. When
 * compared with <code>dir2</code>, it will not be equal, as <code>dir2</code>
 * also contains a single group for txt files, but it contains only one such
 * file. Because of the non-matching groups, the directories will not be
 * considered equal either.
 * <p>
 * Example B: {@link #DIFFERENCIATE_NONE_ONE_MULTIPLE}
 * <p>
 * This granularity will still differentiate between the group of one text file
 * and the group with multiple text files, thus having the same effect as in
 * example A.
 * <p>
 * Example C: {@link #GROUP_EXISTENCE}
 * <p>
 * Now the two groups for text files will be equal. Even though they still
 * contain a different number of files, their comparison will simply check if
 * they contain anything at all.
 * 
 */
public enum HashApproach {
	/**
	 * TODO: improve naming and add documentation
	 */
	PREDICATE_ONLY,
	/**
	 * Simply check for the existence of at least one item in a group. When
	 * comparing two groups at least one item must exist in both or in neither for
	 * them to be considered equal.
	 */
	GROUP_EXISTENCE,
	/**
	 * Differentiate between three possible states regarding the number of items
	 * within a group. Considered states are "none", "one" and "multiple".
	 * <p>
	 * It matters if there are no items in a group (i.e. the group does not exist),
	 * only one item in the group or multiple items in the group. However, it does
	 * not matter if a group has two items or more.
	 */
	DIFFERENCIATE_NONE_ONE_MULTIPLE,
	/**
	 * Count each encountered matching item in a group. When comparing two groups
	 * both counts must match for the groups to be considered equal.
	 */
	EXACT_COUNT
}
