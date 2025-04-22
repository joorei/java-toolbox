package org.codeturnery.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Using Java's {@link Proxy} capabilities and this class allows to create an
 * instance <code>X</code> (via {@link #createProxy}) that will act like an
 * actual instance of a specific class or interface, but will actually just
 * redirect method calls to a collection of backing instances.
 * 
 * Each method call on <code>X</code> will be redirected to all {@link #targets
 * target instances}. Each target will be invoked one after another with the
 * same parameters that were given in the original method call to
 * <code>X</code>.
 * 
 * The returned value of each method call to <code>X</code> can be configured
 * from the outside. It will be determined by invoking the
 * {@link #returnFunction} (given on initialization of this class) with the
 * return values of all method calls to the target instances.
 * <p>
 * Note that proper implementation of {@link Object} methods in
 * {@link #returnFunction} is required to make proxies work with
 * equality-sensitive use-cases like {@link HashSet}.
 *
 * @param <T> The (pretended) type of <code>X</code>.
 * @param <R> The aggregated return when <strong>any</strong> method of
 *            {@code X} is called.
 */
public class MergedInvokationHandler<T, R> implements InvocationHandler {
	private final Collection<T> targets;
	private final BiFunction<List<@Nullable Object>, Method, R> returnFunction;

	/**
	 * @param targets
	 * @param returnFunction must be implemented to support all foreseeable calls,
	 *                       including implicit ones by classes like {@link HashSet}
	 */
	protected MergedInvokationHandler(final Collection<T> targets,
			final BiFunction<List<@Nullable Object>, Method, R> returnFunction) {
		this.targets = Checks.requireNonEmpty(targets);
		this.returnFunction = Checks.requireNonNull(returnFunction);
	}

	/**
	 * Will be called automatically when methods of instances of {@code X} are
	 * called. As a result it will call the method with the same name in each item
	 * in {@link #targets}. The return of this method is determined by the
	 * {@link #returnFunction}, which is called with the return values of all target
	 * calls.
	 */
	@Override
	public @Nullable R invoke(final @Nullable Object proxy, final @Nullable Method method,
			final Object @Nullable [] args)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final var nonNullMethod = Checks.requireNonNull(method);
		final List<@Nullable Object> returns = new ArrayList<>(this.targets.size());
		for (final T target : this.targets) {
			returns.add(nonNullMethod.invoke(target, args));
		}
		return this.returnFunction.apply(returns, nonNullMethod);
	}

	/**
	 * Creates the instance <code>X</code> as described by
	 * {@link MergedInvokationHandler}.
	 * 
	 * @param <P>            The (pretended) type of the created proxy.
	 * @param targets        The instances that are called when a proxy method is
	 *                       called.
	 * @param returnFunction The function to aggregate the returns of the targets to
	 *                       a single value which is returned when a proxy method is
	 *                       called.
	 * @param pretendedType  The {@link Class} instance of the pretended type of the
	 *                       proxy.
	 * @return A proxy redirecting its method calls to the given targets.
	 */
	@SuppressWarnings("unchecked")
	public static <P> P createProxy(final Collection<P> targets,
			final BiFunction<List<@Nullable Object>, Method, ?> returnFunction, final Class<P> pretendedType) {
		final var interfaces = new Class<?>[] { pretendedType };
		final var handler = new MergedInvokationHandler<>(targets, returnFunction);
		return (P) Proxy.newProxyInstance(MergedInvokationHandler.class.getClassLoader(), interfaces, handler);
	}
}
