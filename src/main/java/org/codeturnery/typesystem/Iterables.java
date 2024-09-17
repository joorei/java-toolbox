package org.codeturnery.typesystem;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class Iterables {

	@SuppressWarnings("null")
	public static <E> @NonNull List<@Nullable E> withNullables(final @NonNull List<E> list) {
		return list;
	}
	
	public static <E> List<@Nullable E> nonNullWithNullables(final @Nullable List<E> list) {
		return Iterables.withNullables(Checks.requireNonNull(list));
	}
	
	@SuppressWarnings("null")
	public static <@NonNull E> @NonNull List<E> emptyList() {
		return Collections.emptyList();
	}
	
	@SuppressWarnings("null")
	public static <@NonNull E> @NonNull Set<@NonNull E> emptySet() {
		return Collections.emptySet();
	}
	
	@SuppressWarnings("null")
	@SafeVarargs
	public static <@NonNull E> @NonNull Set<@NonNull E> setOf(final @NonNull E @NonNull... elements) {
		return Set.of(elements);
	}
}
