package org.codeturnery.tree;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Predicate to match strings using prefixes and suffixes.
 * 
 * @param <T> the type of nodes this predicate can be applied on
 */
abstract public class StringMatcher<T> implements Predicate<T> {

	private final Set<String> suffixes;
	private final Set<String> prefixes;
	private final boolean conjunction;

	/**
	 * @param suffixes    the suffixes against strings should be checked
	 * @param prefixes    the prefixes against strings should be checked
	 * @param conjunction If <code>true</code>, at least one suffix and one prefix
	 *                    must match for a positive evaluation; if
	 *                    <code>false</code>, either one suffix or one prefix must
	 *                    match for a positive evaluation.
	 */
	public StringMatcher(final Set<String> suffixes, final Set<String> prefixes, final boolean conjunction) {
		this.suffixes = suffixes;
		this.prefixes = prefixes;
		this.conjunction = conjunction;
	}

	@Override
	public boolean test(final T value) {
		final String string = getString(value);
		return this.conjunction ? testSuffixes(string) && testPrefixes(string)
				: testSuffixes(string) || testPrefixes(string);
	}

	/**
	 * Will always return <code>false</code>, if no {@link #suffixes} are set.
	 * 
	 * @param string the string to test the suffixes on
	 * @return <code>true</code> if the given string ends with any of the
	 *         {@link #suffixes} of this instance. <code>false</code> otherwise.
	 */
	protected boolean testSuffixes(final String string) {
		for (final String suffix : this.suffixes) {
			if (string.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Will always return <code>false</code>, if no {@link #prefixes} are set.
	 * 
	 * @param string the string to test the prefixes on
	 * @return <code>true</code> if the given string ends with any of the
	 *         {@link #prefixes} of this instance. <code>false</code> otherwise.
	 */
	protected boolean testPrefixes(final String string) {
		for (final String prefix : this.prefixes) {
			if (string.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	abstract protected String getString(final T node);
}
