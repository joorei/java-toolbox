package org.codeturnery.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Proxies created by this handler will receive method calls but will not pass
 * them on immediately. Instead, the calls are cached. Only when the
 * {@link #passOn(Object)} method is called, the cached calls will be passed on
 * to the given target.
 * <p>
 * Because the actual return of the target is unknown when a proxy method is
 * called, the proxy will always return a fixed value given on instantiation,
 * which works best with void-methods.
 *
 * @param <T>
 */
public class StallingInvokationHandler<T> implements InvocationHandler {

	private final List<MethodCall> stalledCalls;
	private final @Nullable Object returnValue;

	public StallingInvokationHandler(final @Nullable Object returnValue) {
		this.stalledCalls = new ArrayList<>();
		this.returnValue = returnValue;
	}

	@Override
	public @Nullable Object invoke(final @Nullable Object proxy, final @Nullable Method method,
			final Object @Nullable [] args) {
		this.stalledCalls.add(new MethodCall(method, args));
		return this.returnValue;
	}

	/**
	 * Pass on all method calls to the target in the (general) order they were
	 * called on the proxy.
	 * 
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<Method, @Nullable Object> passOn(final T target) throws Exception {
		final LinkedHashMap<Method, @Nullable Object> returns = new LinkedHashMap<>(this.stalledCalls.size());
		for (final MethodCall stalledCall : this.stalledCalls) {
			final Method method = stalledCall.getMethod();
			final Object[] args = stalledCall.getArguments();
			returns.put(method, method.invoke(target, args));
		}
		return returns;
	}

	public static <P> P createProxy(final Class<P> pretendedType, final @Nullable Object returnValue) {
		final var interfaces = new Class<?>[] { pretendedType };
		final var handler = new StallingInvokationHandler<>(returnValue);
		return (P) Proxy.newProxyInstance(StallingInvokationHandler.class.getClassLoader(), interfaces, handler);
	}
}
