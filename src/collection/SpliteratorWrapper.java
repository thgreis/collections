package collection;

import java.util.function.Consumer;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;

/**
 * A {@code Spliterator} wrapper utility class.
 * 
 * A {@code SpliteratorWrapper} is used to decorate a spliterator
 * and to override {@link #getComparator() getComparator} method.
 * 
 * @param <E> the type of elements returned by this spliterator
 * 
 * @author Thiago Reis
 * @see java.util.Spliterator
 */
public final class SpliteratorWrapper<E> implements Spliterator<E> {
	//immutable state
	private final Spliterator<E> wrapee;
	private final Comparator<? super E> comparator;

	//constructors
	public SpliteratorWrapper(Spliterator<E> wrapee) {
		this.wrapee = Objects.requireNonNull(wrapee, "Invalid null wrapee.");
		this.comparator = wrapee.getComparator();
	}

	public SpliteratorWrapper(Spliterator<E> wrapee, Comparator<? super E> comparator) {
		this.wrapee = Objects.requireNonNull(wrapee, "Invalid null wrapee.");
		this.comparator = Objects.requireNonNull(comparator, "Invalid null comparator.");
	}

	//spliterator behaviour
	@Override
	public boolean tryAdvance(Consumer<? super E> action) {
		return wrapee.tryAdvance(Objects.requireNonNull(action, "Invalid null action."));
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		wrapee.forEachRemaining(Objects.requireNonNull(action, "Invalid null action."));
	}

	@Override
	public Spliterator<E> trySplit() {
		return wrapee.trySplit();
	}

	@Override
	public long estimateSize() {
		return wrapee.estimateSize();
	}

	@Override
	public long getExactSizeIfKnown() {
		return wrapee.getExactSizeIfKnown();
	}

	@Override
	public int characteristics() {
		return wrapee.characteristics();
	}

	@Override
	public boolean hasCharacteristics(int characteristics) {
		return wrapee.hasCharacteristics(characteristics);
	}

	@Override
	public Comparator<? super E> getComparator() {
		return comparator;
	}

	//object behaviour
	@Override
	public int hashCode() {
		return Objects.hash(wrapee, comparator);
	}

	@Override
	public boolean equals(Object object) {
		return object == this || (
			object instanceof SpliteratorWrapper
			&& wrapee.equals(((SpliteratorWrapper)object).wrapee)
			&& comparator.equals(((SpliteratorWrapper)object).comparator)
		);
	}

	@Override
	public String toString() {
		return "Spliterator=" + wrapee + ", Comparator=" + comparator;
	}
}
