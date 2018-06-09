package collection;

import java.util.stream.StreamSupport;
import java.util.stream.Stream;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Collection;
import java.util.Iterator;

/**
 * An {@code Iterable} with the {@code Collection} query methods.
 * 
 * A {@code Container} is a utility interface used to define
 * the collection query methods over an {@code Iterable}.
 * 
 * @param <E> the type of elements in this container
 * 
 * @author Thiago Reis
 * @see java.lang.Iterable
 * @see java.util.Collection
 */
public interface Container<E> extends Iterable<E> {
	//container behaviour
	int size();
	boolean isEmpty();
	boolean contains(Object object);
	boolean containsAll(Collection<?> collection);
	Object[] toArray();
	<T> T[] toArray(T[] array);

	@Override
	default Spliterator<E> spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(), 0);
	}

	default Stream<E> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	default Stream<E> parallelStream() {
		return StreamSupport.stream(spliterator(), true);
	}

	//iterable behaviour
	@Override Iterator<E> iterator();
}
