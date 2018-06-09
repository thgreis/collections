package collection;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractIterator<E> implements Iterator<E> {
	//mutable state
	private int modification;
	private boolean removable;

	/**
	 * Sole constructor. (for invocation by subclass constructors, typically implicit.)
	 */
	protected AbstractIterator() {
		//empty
	}

	//abstract iterator behaviour
	protected final void checkNext(boolean hasNext) {
		//use in next()
		if (!hasNext) {
			throw new NoSuchElementException("Iteration has no more elements.");
		}
	}

	protected final void setRemovable(boolean removable) {
		//used in next() and remove()
		this.removable = removable;
	}

	protected final void checkRemovable() {
		//used in remove()
		if (!removable) {
			throw new IllegalStateException("The iterator.next() method has not yet been called, or the iterator.remove() method has already been called after the last call to the iterator.next() method.");
		}
	}

	protected final void setModification(int modification) {
		//used in remove()
		this.modification = modification;
	}

	protected final void checkModification(int modification) {
		//used in next() and remove()
		if (this.modification != modification) {
			throw new ConcurrentModificationException("The collection has been modified during the iteration.");
		}
	}

	//object behaviour
	@Override
	public int hashCode() {
		return Objects.hash(modification, removable);
	}

	@Override
	public boolean equals(Object object) {
		return object == this || (
			object instanceof AbstractIterator
			&& modification == ((AbstractIterator)object).modification
			&& removable == ((AbstractIterator)object).removable
		);
	}

	@Override
	public String toString() {
		return "Modification=" + modification + ", Removable=" + removable;
	}
}
