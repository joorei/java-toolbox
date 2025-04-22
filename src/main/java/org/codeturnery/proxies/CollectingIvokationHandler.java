package org.codeturnery.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.Nullable;

/**
 * <strong>TODO: currently created proxies are quite limited in their usage, as
 * they do not support {@link Object} methods like {@link Object#hashCode} or
 * {@link Object#equals}. E.g. they can't be used in a {@link HashSet}.</strong>
 * <p>
 * Proxies created by this handler will receive method calls but will not pass
 * them on to any target by themselves. Instead, the calls are stored for
 * retrieval via the {@link #getCollectedCalls(Object)} method.
 * <p>
 * Because the actual return of the target is unknown when a proxy method is
 * called, the proxy will always return a fixed value given on instantiation of
 * this class and thus works best with void-methods.
 * 
 * @param <T> the (pretended) type of the proxies created by this handler
 */
@SuppressWarnings({ "unchecked" })
public class CollectingIvokationHandler<T> implements InvocationHandler {

	private final @Nullable Object returnValue;
	private final List<T> proxies = new ArrayList<>();
	private final Function<T, List<MethodCall>> newList = proxy -> new ArrayList<>();
	private final Map<T, List<MethodCall>> stalledCalls = new LinkedHashMap<>();
	private final Class<T> pretendedType;

	public CollectingIvokationHandler(final Class<T> pretendedType, final @Nullable Object returnValue) {
		this.pretendedType = Checks.requireNonNull(pretendedType);
		this.returnValue = returnValue;
	}

	@Override
	public @Nullable Object invoke(final Object proxy, final Method method, final Object @Nullable [] args) {
		Checks.requireNonNull(method);
		if (this.pretendedType.isInstance(proxy)) {
			final MethodCall stalledCall = new MethodCall(method, args == null ? new Object[] {} : args);
			final List<MethodCall> stalledCallsOfProxy = this.stalledCalls.computeIfAbsent((T) proxy, this.newList);
			stalledCallsOfProxy.add(stalledCall);
			return this.returnValue;
		}

		throw new IllegalArgumentException("handler was called with unexpected proxy type");
	}

	public List<MethodCall> getCollectedCalls(final T proxy) {
		return Collections.unmodifiableList(this.stalledCalls.getOrDefault(proxy, Collections.emptyList()));
	}

	public void clearAll() {
		this.stalledCalls.clear();
	}

	public void clear(final T proxy) {
		this.stalledCalls.remove(proxy);
	}

	public T createProxy() {
		final var interfaces = new Class<?>[] { this.pretendedType };
		final T proxy = (T) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, this);
		this.proxies.add(proxy);
		return proxy;
	}
}
