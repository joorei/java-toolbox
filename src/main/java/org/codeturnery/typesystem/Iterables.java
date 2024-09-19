package org.codeturnery.typesystem;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Provides type conversions for iterable and similar data types, especially to
 * set the correct nullable/non-nullable types of contained elements.
 */
public class Iterables {

	/**
	 * Adjust the type information of the given list, so that it can explicitly
	 * contain <code>null</code> elements.
	 * 
	 * @param <E>  the type of the elements of the list
	 * @param list the list that shall be allowed to contain <code>null</code>
	 *             elements
	 * @return the given list with adjusted type information
	 */
	@SuppressWarnings("null")
	public static <E> @NonNull List<@Nullable E> withNullables(final @NonNull List<E> list) {
		return list;
	}

	/**
	 * Adjust the type information of the given list, so that it can explicitly
	 * contain <code>null</code> elements.
	 * <p>
	 * The method will automatically throw a {@link NullPointerException} exception
	 * if the given list is <code>null</code> itself.
	 * 
	 * @param <E>  the type of the elements of the list
	 * @param list the list that shall be allowed to contain <code>null</code>
	 *             elements
	 * @return the given list with adjusted type information
	 * @throws NullPointerException thrown if the given list is <code>null</code>
	 *                              itself
	 */
	public static <E> List<@Nullable E> nonNullWithNullables(final @Nullable List<E> list) throws NullPointerException {
		return Iterables.withNullables(Checks.requireNonNull(list));
	}

	/**
	 * Creates an empty list with the explicit type information that it can and does
	 * not contain <code>null</code> elements
	 * 
	 * @param <E> the type of the (theoretical) elements in the list
	 * @return the created empty list
	 */
	@SuppressWarnings("null")
	public static <@NonNull E> @NonNull List<E> emptyList() {
		return Collections.emptyList();
	}

	/**
	 * Creates an empty set with the explicit type information that it can and does
	 * not contain <code>null</code> elements
	 * 
	 * @param <E> the type of the (theoretical) elements in the set
	 * @return the created empty set
	 */
	@SuppressWarnings("null")
	public static <@NonNull E> @NonNull Set<@NonNull E> emptySet() {
		return Collections.emptySet();
	}

	/**
	 * Creates an empty steam with the explicit type information that it can and
	 * does not contain <code>null</code> elements
	 * 
	 * @param <E> the type of the (theoretical) elements in the stream
	 * @return the created empty stream
	 */
	@SuppressWarnings("null")
	public static <@NonNull E> @NonNull Stream<@NonNull E> emptyStream() {
		return Stream.empty();
	}

	/**
	 * Creates a set containing the given elements with the explicit type
	 * information that it can and does not contain <code>null</code> elements
	 * 
	 * @param <E>      the type of the elements in the set
	 * @param elements the elements to create a set for
	 * @return the created stream
	 */
	@SuppressWarnings("null")
	@SafeVarargs
	public static <@NonNull E> @NonNull Set<@NonNull E> setOf(final @NonNull E @NonNull... elements) {
		return Set.of(elements);
	}

}
