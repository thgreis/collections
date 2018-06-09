package collection.bag;

import collection.SpliteratorWrapper;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators;

public interface SortedBag<E> extends Bag<E> {
	Comparator<? super E> comparator();
	SortedBag<E> subBag(E fromElement, E toElement);
	SortedBag<E> headBag(E toElement);
	SortedBag<E> tailBag(E fromElement);
	E first();
	E last();

	@Override
	default Spliterator<E> spliterator() {
		return new SpliteratorWrapper(Spliterators.spliterator(SortedBag.this, Spliterator.SORTED | Spliterator.ORDERED), SortedBag.this.comparator());
	}
}
