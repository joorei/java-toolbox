package org.codeturnery.typesystem;

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

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

	@SuppressWarnings("null")
	public static <T> @NonNull Optional<@NonNull T> empty() {
		return Optional.empty();
	}

	public static boolean anyPresent(final Optional<?>... optionals) {
		for (final Optional<?> optional : optionals) {
			if (optional.isPresent()) {
				return true;
			}
		}
		return false;
	}
}
