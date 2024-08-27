package org.codeturnery.proxies;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.Nullable;

public class LockableProxyFactory<T> implements InvocationHandler {
	private @Nullable T target;
	private final Class<T> pretendedType;

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

	@SuppressWarnings("unchecked")
	public T createProxy() {
		final var interfaces = new Class<?>[] { this.pretendedType };
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, this);
	}

	public void lock() {
		this.target = null;
	}

	public boolean isLocked() {
		return this.target == null;
	}
}
