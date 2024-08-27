package org.codeturnery.typesystem;

import java.util.List;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

public class Iterables {

	public static <E> @NonNull List<@Nullable E> withNullables(@NonNull List<E> list) {
		return list;
	}
	
	public static <E> @NonNull List<@Nullable E> nonNullWithNullables(@Nullable List<E> list) {
		return Iterables.withNullables(Checks.requireNonNull(list));
	}
}
