package org.codeturnery.characters;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Can be filled with a predetermined maximum number of {@link String} instances
 * and behaves as if those instances were a single {@link CharSequence}.
 * <p>
 * Beside the circumstantial performance advantages explained below, the
 * approach allows to set different parts of the {@link CharSequence} in any
 * order and to replace them at any time.
 * <p>
 * However, due to the used approach, instances of this class are by no means
 * immutable.
 * <p>
 * Compared to a class like {@link StringBuilder} the usage of references avoids
 * copying around the individual characters until
 * {@link StringSequence#toString} is called, which merges all
 * backing instances into a single {@link String}. This makes adding or
 * replacing backing {@link CharSequence} instances very fast, and taking
 * {@link StringSequence#subSequence subSequences} can be very fast
 * too as it too avoids copying characters as much as possible.
 * <p>
 * To allow for fast comparisons, two instances of this class are equal if all
 * backing instances are equal at their corresponding position at the time of
 * the comparison. I.e. two instances using different backing instances are not
 * equal, even if they represent the same sequence of characters, as this would
 * require every single character.
 * <p>
 * This means that this class is ideal to put a {@link CharSequence} together
 * from multiple {@link String} instances, potentially in different stages of
 * the application. The more {@link String} instances are involved and the
 * larger they are, the more advantage has this class over alternatives that
 * copy characters around like {@link StringBuilder}.
 */
final public class StringSequence implements CharSequence {

	private final String[] references;
	private int length;
	private @Nullable String string;

	/**
	 * @param referenceCount How many references to {@link CharSequence} instances
	 *                       this instance can initially hold. Fixed value. No
	 *                       manual resizing is possible. No automatic resizing will
	 *                       be done.
	 */
	public StringSequence(final int referenceCount) {
		this.references = new String[referenceCount];
	}

	/**
	 * Directly stores the given instance in this class at the defined position.
	 * <p>
	 * {@code null} values as well as unset indexes will be ignored when handling
	 * the references internally.
	 * 
	 * @param index
	 * @param reference
	 */
	public void setReference(final int index, final @Nullable String reference) {
		if (this.references[index] != null) {
			this.length -= this.references[index].length();
		}
		this.length += reference.length();
		this.string = null;
		this.references[index] = reference;
	}

	/**
	 * Removes all backing instances from this instance. Does not change the
	 * {@code referenceCount} set on initialization.
	 */
	public void clear() {
		Arrays.fill(this.references, null);
		this.length = 0;
		this.string = null;
	}

	@Override
	public int length() {
		return this.length;
	}

	@Override
	public char charAt(int position) {
		if (position < 0 || position >= this.length) {
			throw new IndexOutOfBoundsException(position);
		}
		int remainingPosition = position;
		int currentLength;
		CharSequence currentReference;

		// iterate through the references and find
		// the one containing the given position
		for (int i = 0; i < this.references.length; i++) {
			currentReference = this.references[i];
			currentLength = currentReference == null ? 0 : currentReference.length();
			if (currentLength > remainingPosition) {
				// position is in current array
				return currentReference.charAt(remainingPosition);
			}
			// position is in one of the following arrays
			remainingPosition -= currentLength;
		}

		throw new IndexOutOfBoundsException(position);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		if (start > end || start < 0 || end > this.length) {
			throw new IndexOutOfBoundsException("Invalid start (" + start + ") or end (" + end + ").");
		}

		if (start == end) {
			return "";
		}

		if (start == 0 && end == this.length) {
			return this;
		}

		int localStart = start;
		int startReferenceIndex = 0;
		int endReferenceIndex = 0;
		int skippedCharacters = 0;

		// Find the start reference index and the corresponding localStart
		for (; startReferenceIndex < this.references.length; startReferenceIndex++) {
			final CharSequence currentReference = this.references[startReferenceIndex];
			if (currentReference != null) {
				int currentLength = currentReference.length();
				if (localStart < currentLength) {
					// The start is within this reference
					break;
				}
				localStart -= currentLength;
				skippedCharacters += currentLength;
			}
		}

		int localEnd = end - skippedCharacters;

		// Now we can skip ahead and start at the found startReferenceIndex for end
		// reference index
		for (endReferenceIndex = startReferenceIndex; endReferenceIndex < this.references.length; endReferenceIndex++) {
			final CharSequence currentReference = this.references[endReferenceIndex];
			if (currentReference != null) {
				int currentLength = currentReference.length();
				if (localEnd <= currentLength) {
					break;
				}
				localEnd -= currentLength;
			}
		}

		// we now know which strings are relevant for the sub-sequence and can return
		// them
		final int newReferenceCount = endReferenceIndex - startReferenceIndex + 1;
		switch (newReferenceCount) {
		case 1:
			return this.references[startReferenceIndex].subSequence(localStart, localEnd);
		default:
			final var result = new StringSequence(newReferenceCount);
			final String startSequence = this.references[startReferenceIndex];
			result.setReference(0, startSequence.substring(localStart, startSequence.length()));
			final String endSequence = this.references[endReferenceIndex];
			result.setReference(newReferenceCount - 1, endSequence.substring(0, localEnd));
			// Fill the new sequence with references between the first and last one
			for (int i = 1; i < newReferenceCount - 1; i++) {
				result.setReference(i, this.references[i + startReferenceIndex]);
			}
			return result;
		}
	}

	@Override
	public String toString() {
		if (this.string == null) {
			final var builder = new StringBuilder(this.length);
			for (int i = 0; i < this.references.length; i++) {
				if (this.references[i] != null) {
					builder.append(this.references[i]);
				}
			}
			this.string = builder.toString();
		}
		return this.string;
	}
	

	/**
	 * As {@link CharSequence} does not define its own {@code equals} method
	 * contract and it has to work symmetrical, this method will only consider other
	 * instances of this class (or subclasses) as potentially equal. I.e. even if a
	 * given {@link String} represents the same sequence of characters as this
	 * instance, this method must return false.
	 * <p>
	 * Consequently, using general {@link CharSequence} instances in {@link Set}s or
	 * as keys in {@link Map}s is not recommended.
	 * <p>
	 * For a given instance of this class/subclasses, this method will return
	 * {@code true}, if they represent the same sequence of characters to the
	 * outside world, even if their internal state differs.
	 * <p>
	 * Subclasses have to honor this equality contract and implement their
	 * {@link #hashCode} method accordingly.
	 */
	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof StringSequence)) {
			return false;
		}

		final StringSequence other = (StringSequence) obj;
		
		// As we only consider instances to be equal if they use the same references,
		// the number of references must be the same
		if (this.references.length != other.references.length) {
			return false;
		}
		
		// If the two instances do not represent a sequence of characters with the same
		// length, they can't be considered equal.
		if (this.length != other.length) {
			return false;
		}

		// compare the references
		for (int i = 0; i < this.references.length; i++) {
			if (this.references[i] != null) {
				if (!this.references[i].equals(other.references[i])) {
					return false;
				}
			} else if (other.references[i] != null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.references);
	}
}
