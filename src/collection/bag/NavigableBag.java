package collection.bag;

import java.util.Iterator;

public interface NavigableBag<E> extends SortedBag<E> {
	E lower(E element);
	E floor(E element);
	E ceiling(E element);
	E higher(E element);
	E pollFirst();
	E pollLast();
	Iterator<E> descendingIterator();
	NavigableBag<E> descendingBag();
	NavigableBag<E> subBag(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive);
	NavigableBag<E> headBag(E toElement, boolean inclusive);
	NavigableBag<E> tailBag(E fromElement, boolean inclusive);
}
