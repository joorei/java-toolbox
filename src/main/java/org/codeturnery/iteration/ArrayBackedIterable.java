package org.codeturnery.iteration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @param <T>
 */
public class ArrayBackedIterable<T> implements Iterable<T> {

	private final List<T[]> arrays;
	private int totalSize = 0;

	public ArrayBackedIterable() {
		this.arrays = new ArrayList<>();
	}

	public ArrayBackedIterable(final int expectedArrays) {
		this.arrays = new ArrayList<>(expectedArrays);
	}

	public void addArray(T[] values) {
		this.arrays.add(values);
		this.totalSize += values.length;
	}

	public void addValues(T... values) {
		addArray(values);
	}

	/**
	 * TODO: with the current class' implementation, this method is quite wasteful,
	 * creating a new array with a single value on every call.
	 * 
	 * @param value
	 */
	public void addValue(T value) {
		addValues(value);
	}

	public int size() {
//		int size = 0;
//		for (int i = 0; i < this.arrays.size(); i++) {
//			size += this.arrays.get(i).length;
//		}
//		return size;
		return this.totalSize;
	}

	public T get(int index) {
		if (index < 0 || index >= this.totalSize) {
			throw new IndexOutOfBoundsException(index);
		}

		for (int i = 0; i < this.arrays.size(); i++) {
			final T[] currentArray = this.arrays.get(i);
			if (currentArray.length > index) {
				return currentArray[index];
			}
			index -= currentArray.length;
		}
		throw new IndexOutOfBoundsException(index);
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int currentIndex = 0;

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException("This iterator has no more elements");
				}
				return get(this.currentIndex++);
			}

			@Override
			public boolean hasNext() {
				return this.currentIndex < size();
			}
		};
	}
}
