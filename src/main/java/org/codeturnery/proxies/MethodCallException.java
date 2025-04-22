package org.codeturnery.proxies;

import org.eclipse.jdt.annotation.Checks;

public class MethodCallException extends Exception {

	private static final long serialVersionUID = 4964748825707284540L;
	private final MethodCall methodCall;

	public MethodCallException(final MethodCall methodCall, final Throwable cause) {
		super(cause);
		this.methodCall = Checks.requireNonNull(methodCall);
	}

	public MethodCall getMethodCall() {
		return this.methodCall;
	}
	
}
