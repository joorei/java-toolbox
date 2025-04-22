package org.codeturnery.proxies;

import java.lang.reflect.Method;

import org.eclipse.jdt.annotation.Checks;
import org.eclipse.jdt.annotation.Nullable;

public class MethodCall {
	private final Method method;
	private final Object @Nullable [] arguments;

	public MethodCall(final Method method, final Object @Nullable [] args) {
		this.method = Checks.requireNonNull(method);
		this.arguments = Checks.requireNonNull(args);
	}

	public Method getMethod() {
		return this.method;
	}

	public Object @Nullable [] getArguments() {
		return this.arguments;
	}

	public void invoke(final Object target) throws MethodCallException {
		try {
			this.method.invoke(target, this.arguments);
		} catch (final Throwable exception) {
			throw new MethodCallException(this, exception);
		}
	}
}
