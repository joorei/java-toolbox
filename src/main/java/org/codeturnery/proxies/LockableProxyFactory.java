package org.codeturnery.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.Nullable;

/**
 * Create a proxy based on a target instance that can be disabled "remotely".
 * <p>
 * An instance of this class is bound to one specific target instance on
 * initialization. It can create as many proxies as needed via
 * {@link #createProxy}, each one backed by the same target instance. Using
 * {@link #lock}, all proxies will stop allowing access to the target instance
 * and throw a {@link LockedException} if access is attempted.
 * 
 * @param <T> the actual type of the target instance
 */
public class LockableProxyFactory<T> implements InvocationHandler {
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
