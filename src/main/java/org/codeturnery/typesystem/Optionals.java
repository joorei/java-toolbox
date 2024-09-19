package org.codeturnery.typesystem;

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Provides type conversions for {@link Optional} instances, especially to set
 * the correct nullable/non-nullable types of contained elements.
 */
public class Optionals {

	/**
	 * 
	 * @param <T>   the type of the value to be wrapped in the {@link Optional}
	 * @param value the value to be wrapped in the {@link Optional}
	 * @return If the given value is non-<code>null</code>, an non-empty
	 *         {@link Optional} instance containing the value. An empty
	 *         {@link Optional} instance otherwise.
	 */
	@SuppressWarnings("null")
	public static <T> @NonNull Optional<@NonNull T> ofNullable(final @Nullable T value) {
		return Optional.ofNullable(value);
	}

	/**
	 * @param <T>   the type of the value to be wrapped in the {@link Optional}
	 * @param value the value to be wrapped in the {@link Optional}
	 * @return An non-empty {@link Optional}
	 * @throws NullPointerException if the given value is <code>null</code>.
	 */
	@SuppressWarnings("null")
	public static <@NonNull T> Optional<T> of(final T value) {
		return Optional.of(value);
	}

	/**
	 * @param <T> the type of the (theoretically) contained value
	 * @return an empty optional, with the explicit type information that the
	 *         (theoretically) contained value can not be <code>null</code>
	 */
	@SuppressWarnings("null")
	public static <T> @NonNull Optional<@NonNull T> empty() {
		return Optional.empty();
	}

	/**
	 * Tests if in any of the given {@link Optional} instances a value is present.
	 * 
	 * @param optionals the instances to test
	 * @return <code>true</code>, if any of the given instances contains a value,
	 *         <code>false</code> otherwise
	 */
	public static boolean anyPresent(final Optional<?>... optionals) {
		for (final Optional<?> optional : optionals) {
			if (optional.isPresent()) {
				return true;
			}
		}
		return false;
	}
}
