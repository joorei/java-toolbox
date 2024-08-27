package org.codeturnery.typesystem;

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

@org.eclipse.jdt.annotation.NonNullByDefault
public class Optionals {

	/**
	 * 
	 * @param <T>
	 * @param v
	 * @return
	 */
	public static <T> @NonNull Optional<@NonNull T> ofNullable(@Nullable T v) {
		return Optional.ofNullable(v);
	}
	/**
	 * 
	 * @param <T>
	 * @param v
	 * @return
	 */
	public static <@NonNull T> @NonNull Optional<T> of(@Nullable T v) {
		return Optional.of(v);
	}
	
	public static <T> @NonNull Optional<@NonNull T> empty() {
		return Optional.empty();
	}
	
	public static boolean anyPresent(Optional<?> ...optionals) {
		for (final Optional<?> optional : optionals) {
			if (optional.isPresent()) {
				return true;
			}
		}
		return false;
	}
}
