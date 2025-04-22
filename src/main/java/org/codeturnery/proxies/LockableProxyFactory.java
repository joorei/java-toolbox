package org.codeturnery.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Create a proxy based on a target instance that can be disabled "remotely".
 * <p>
 * An instance of this class is bound to one specific target instance on
 * initialization. It can create as many proxies as needed via
 * {@link #createProxy}, each one backed by the same target instance. Using
 * {@link #lock}, all proxies will stop allowing access to the target instance
 * and throw a {@link LockedException} if access (i.e. calling methods) is
 * attempted.
 * 
 * @param <T> the actual type of the target instance
 */
public class LockableProxyFactory<T> implements InvocationHandler {
	/**
	 * Set to non-{@code null} on instantiation, but set to {@code null} when this
	 * proxy is locked.
	 */
	private @Nullable T target;
	private final Class<T> pretendedType;

	/**
	 * 
	 * @param target        The target instance wrapped by the proxies.
	 * @param pretendedType Passed to {@link Proxy#newProxyInstance} as
	 *                      <code>interfaces</code>
	 */
	public LockableProxyFactory(final T target, final Class<T> pretendedType) {
		this.target = Checks.requireNonNull(target);
		this.pretendedType = Checks.requireNonNull(pretendedType);
	}

	@Override
	public @Nullable Object invoke(final @Nullable Object proxy, final @Nullable Method method,
			final Object @Nullable [] arguments)
			throws LockedException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (isLocked()) {
			throw new LockedException();
		}

		return Checks.requireNonNull(method).invoke(this.target, arguments);
	}

	/**
	 * Provides the given {@code invoker} with a temporary proxy instance instead of
	 * the actual {@code target}.
	 * <p>
	 * For the given {@code target} a proxy instance is created and provided to the
	 * given {@code invoker}. The invoker is expected to use the proxy's methods
	 * before returning. When it returns, any access to the proxy will be denied, at
	 * which point this method returns too.
	 * 
	 * @param <X>     the type of the target whose methods shall be called
	 * @param target  the instance whose methods shall be called via a proxy
	 * @param invoker the instance provided with the proxy to call its methods
	 */
	public static <X> void provideAndLock(final X target, final Class<X> pretendedType, final Consumer<X> invoker) {
		// create the handler that manages access to the target via proxies
		final var invokationHandler = new LockableProxyFactory<>(target, pretendedType);
		// create a proxy instance
		final X proxy = invokationHandler.createProxy();
		// give the proxy to the invoker so that it can call the proxy's methods
		invoker.accept(proxy);
		// the invoker is done using the proxy, so we lock it to prevent further access
		invokationHandler.lock();
	}

	/**
	 * Create a proxy instance wrapped by the target of this instance.
	 * 
	 * @return the created proxy instance
	 */
	@SuppressWarnings("unchecked")
	public T createProxy() {
		final var interfaces = new Class<?>[] { this.pretendedType };
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, this);
	}

	/**
	 * Disallow any access to any of the methods of the target instance.
	 */
	public void lock() {
		this.target = null;
	}

	/**
	 * @return <code>true</code> if {@link #lock} was called, <code>false</code>
	 *         otherwise
	 */
	public boolean isLocked() {
		return this.target == null;
	}
}
