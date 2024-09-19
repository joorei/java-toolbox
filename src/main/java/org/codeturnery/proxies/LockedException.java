package org.codeturnery.proxies;

/**
 * Implies that access to a proxy instance created by
 * {@link LockableProxyFactory} was attempted after
 * {@link LockableProxyFactory#lock} was called on the creating instance.
 */
public class LockedException extends RuntimeException {

	private static final long serialVersionUID = 8519039146980242293L;

	LockedException() {
		super();
	}
}
