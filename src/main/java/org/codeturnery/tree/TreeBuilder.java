package org.codeturnery.tree;

import java.util.Comparator;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

/**
 * This class provides an algorithm to build a tree hierarchy from a list of
 * elements with an intentional sorting, resulting in structured
 * {@link TreeNodeInterface}s.
 * <p>
 * Given elements will must be first sorted so that a parent is always placed
 * before its child. Thus the actual algorithm starts with a list like the
 * following:
 * <ol>
 * <li><code>A</code>
 * <li><code>B</code>
 * <li><code>Bb</code>
 * <li><code>Ba</code>
 * <li><code>C</code>
 * </ol>
 * <p>
 * It will then loop through the elements a single time. For each element the
 * previous element will be used to determine the parent. The first element will
 * be placed in a <code>root</code> node which is a valid parent for every
 * element.
 * <p>
 * Step 1:
 * <ol>
 * <li><strong><code>A</code> → first element, thus it is placed inside
 * <code>root</code></strong>
 * <li><code>B</code>
 * <li><code>Bb</code>
 * <li><code>Ba</code>
 * <li><code>C</code>
 * </ol>
 * <p>
 * Step 2:
 * <ol>
 * <li><code>root</code>/<code>A</code>
 * <li><strong><code>B</code> → the previous node (<code>A</code>) is not a
 * valid parent, but its parent (<code>root</code>) is</strong>
 * <li><code>Bb</code>
 * <li><code>Ba</code>
 * <li><code>C</code>
 * </ol>
 * <p>
 * Step 3:
 * <ol>
 * <li><code>root</code>/<code>A</code>
 * <li><code>root</code>/<code>B</code>
 * <li><strong><code>Bb</code> → the previous node (<code>B</code>) is a valid
 * parent</strong>
 * <li><code>Ba</code>
 * <li><code>C</code>
 * </ol>
 * <p>
 * Step 4:
 * <ol>
 * <li><code>root</code>/<code>A</code>
 * <li><code>root</code>/<code>B</code>
 * <li><code>root</code>/<code>B</code>/<code>Bb</code>
 * <li><strong><code>Ba</code> → the previous node (<code>Bb</code>) is not a
 * valid parent, but its parent (<code>B</code>) is</strong>
 * <li><code>C</code>
 * </ol>
 * <p>
 * Step 5:
 * <ol>
 * <li><code>root</code>/<code>A</code>
 * <li><code>root</code>/<code>B</code>
 * <li><code>root</code>/<code>B</code>/<code>Bb</code>
 * <li><code>root</code>/<code>B</code>/<code>Ba</code>
 * <li><strong><code>C</code> → the previous node (<code>Ba</code>) is not a
 * valid parent, its parent (<code>B</code>) is not a valid parent either, but
 * the parent of <code>B</code> (<code>root</code>) is</strong>
 * </ol>
 * The final tree:
 * <ol>
 * <li><code>root</code>/<code>A</code>
 * <li><code>root</code>/<code>B</code>
 * <li><code>root</code>/<code>B</code>/<code>Bb</code>
 * <li><code>root</code>/<code>B</code>/<code>Ba</code>
 * <li><code>root</code>/<code>C</code>
 * </ol>
 * <p>
 * The algorithm supports placing equal elements inside different parents, but
 * the given list must be carefully presorted to make this work. As an example
 * we assume the initial list from the previous example and manually place an
 * additional element <code>X</code> at two different positions inside the list:
 * <ol>
 * <li><code>A</code>
 * <li><code>B</code>
 * <li><code>Bb</code>
 * <li><code>X</code>
 * <li><code>Ba</code>
 * <li><code>C</code>
 * <li><code>X</code>
 * </ol>
 * If we define <code>X</code> to accept any element as parent and apply the
 * algorithm again we will get the following result:
 * <ol>
 * <li><code>root</code>/<code>A</code>
 * <li><code>root</code>/<code>B</code>
 * <li><code>root</code>/<code>B</code>/<code>Bb</code>
 * <li><code>root</code>/<code>B</code>/<code>Bb</code>/<code>X</code>
 * <li><code>root</code>/<code>B</code>/<code>Ba</code>
 * <li><code>root</code>/<code>C</code>
 * <li><code>root</code>/<code>C</code>/<code>X</code>
 * </ol>
 * <p>
 * In general the algorithms seems best suited for trees with few jumps between
 * the node depth and, more importantly, where the elements can be easily
 * pre-sorted to apply the actual algorithm.
 * <p>
 * An example where it can be useful are problematic ZIP files in which the
 * directory delimiter is insufficient to determine the actual tree structure
 * and the order of elements needs to be considered too.
 * 
 * @param <E> The type of the elements to structure.
 * @param <N> The type of the {@link TreeNodeInterface}s to use to structure the
 *            instances.
 */
public abstract class TreeBuilder<E, N extends TreeNodeInterface<N>> {
	/**
	 * Structures the given elements into a tree hierarchy.
	 * <p>
	 * If the given list contains the same element multiple times or if multiple
	 * elements return the same values for the data relevant for the sorting and
	 * grouping then these elements will be positioned as siblings in the hierarchy.
	 * <p>
	 * Changing the state of elements after creating the tree may invalidate the
	 * tree.
	 *
	 * @param elements The elements to build a tree from.
	 * @return The root nodes (those without parent) of the tree.
	 * @throws NullPointerException Neither the given {@link List} nor its elements
	 *                              must be <code>null</code>.
	 */
	public N createTreeFromUnsorted(final List<E> elements) throws NullPointerException {
		sort(elements);
		return createTreeFromSorted(elements);
	}

	/**
	 * Like {@link #createTreeFromUnsorted(List)} but assumes the given {@link List}
	 * was already sorted following the requirements named in {@link #sort(List)}.
	 * <p>
	 * Using the sorting the list can be looped exactly one time. Each element is
	 * added as child to the most recent element found that is deemed a valid parent
	 * by {@link #isValidParent}.
	 *
	 * @param elements The sorted elements to be structured in a tree hierarchy.
	 *
	 * @return The root node of the tree.
	 *
	 * @see #createTreeFromUnsorted(List)
	 */
	public N createTreeFromSorted(final Iterable<E> elements) {
		final N rootNode = getRootNode();

		N previousNode = rootNode;
		for (final E currentElement : elements) {
			final N currentNode = createTreeNode(currentElement);
			final @Nullable N maybeValidParent = findParent(previousNode, currentNode);
			final N validParent = null == maybeValidParent ? rootNode : maybeValidParent;
			addAsChild(validParent, currentNode);
			previousNode = currentNode;
		}

		return rootNode;
	}

	/**
	 * @param element The element this node is created for. Must not be
	 *                <code>null</code>.
	 * @return The created node. Must not be <code>null</code>.
	 */
	protected abstract N createTreeNode(E element);

	/**
	 * Creates a root node that will be used as the topmost parent of all other
	 * nodes and returned by {@link #createTreeFromSorted(Iterable)} and
	 * {@link #createTreeFromUnsorted(List)}.
	 *
	 * @return The created root node.
	 */
	protected abstract N getRootNode();

	/**
	 * Find the nearest parent element that is considered a parent of the child
	 * node. Starts with the given previous node of the given child node.
	 *
	 * @param previousNode The first {@link TreeNodeInterface} to consider as a
	 *                     parent. If it is not a valid parent of the child node
	 *                     than its parent node will be considered as a parent and
	 *                     so on.
	 * @param childNode    The {@link TreeNodeInterface} to find a valid parent for.
	 * @return A parent node deemed valid for the child node by
	 *         {@link #isValidParent(TreeNodeInterface, TreeNodeInterface)} or the
	 *         root node (the node without a parent node) if none other was valid.
	 */
	protected @Nullable N findParent(final N previousNode, final N childNode) {
		N newParent = previousNode;
		// stop searching for a valid parent if the root was reached or a valid parent
		// was found
		while (newParent.getParent() != null && !isValidParent(newParent, childNode)) {
			// FIXME: what should happen in case of `null`?
			newParent = newParent.getParent();
		}
		return newParent;
	}

	/**
	 * Sorts the given elements so that the direct children of an element follow
	 * directly after that element.
	 *
	 * @param elements The list to sort.
	 * @throws NullPointerException Thrown if the given list is <code>null</code>,
	 *                              contains <code>null</code> items or if an item
	 *                              returns <code>null</code> for data necessary to
	 *                              compare the elements.
	 * @see List#sort(Comparator)
	 */
	protected abstract void sort(final List<E> elements) throws NullPointerException;

	/**
	 * Tests if the given parent node is a valid parent for the given child node.
	 *
	 * @param parentNode The node to use as potential parent of the given child
	 *                   node. Will never be a root node (<code>null</code> as value
	 *                   of {@link TreeNodeInterface#getParent()}) as such is always
	 *                   considered a valid parent.
	 * @param childNode  The node to use as potential child in the given parent.
	 * @return true if the parentNode is a valid parent for the given child node.
	 *         false otherwise.
	 */
	protected abstract boolean isValidParent(final N parentNode, final N childNode);

	/**
	 * Adds the given child node into the given parent node.
	 * <p>
	 * The childNode is not necessarily added as a direct child to the parent node.
	 * It may create additional {@link TreeNodeInterface}s if necessary to add
	 * itself as a child inside a child of the parentNode. However it will be added
	 * as a (sub)child inside the parentNode nonetheless, it will
	 * <strong>not</strong> be added into the same level as the parent node or
	 * higher up in the hierarchy.
	 * <p>
	 * The method is also responsible to set the parent of the child correctly if
	 * such thing is necessary.
	 *
	 * @param parentNode The {@link TreeNodeInterface} deemed a valid parent fort
	 *                   the childNode. Must not be <code>null</code>.
	 * @param childNode  The {@link TreeNodeInterface} deemed a valid child or sub
	 *                   child of the parentNode. Must not be <code>null</code>.
	 * @return The {@link TreeNodeInterface} that was placed directly as a child in
	 *         the parentNode.
	 *
	 * @throws NullPointerException If any parameter is <code>null</code>.
	 */
	protected abstract N addAsChild(final N parentNode, final N childNode) throws NullPointerException;
}
