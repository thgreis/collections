//TODO implementar serializable e writeObject()/readObject()

package collection.bag;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Objects;

public class TreeBag<E> extends AbstractMapBag<E> implements NavigableBag<E>, Serializable, Cloneable {
	//static state
	private static final long serialVersionUID = -1L;

	//constructors
	private TreeBag(NavigableMap<E, Counter> map) {
		this.map = map;
	}

	public TreeBag() {
		map = new TreeMap<>();
	}

	public TreeBag(Comparator<? super E> comparator) {
		map = new TreeMap<>(Objects.requireNonNull(comparator, "Invalid null comparator."));
	}

	public TreeBag(Collection<? extends E> collection) {
		Objects.requireNonNull(collection, "Invalid null collection.");

		if (collection instanceof AbstractMapBag) {
			size = ((AbstractMapBag)collection).size;
			map = new TreeMap<>(((AbstractMapBag)collection).map);
		} else {
			map = new TreeMap<>();
			addAll(collection);
		}
	}

	public TreeBag(Map<? extends E, ? extends Number> map) {
		Objects.requireNonNull(map, "Invalid null map.");

		this.map = new TreeMap<>();

		for (Map.Entry<? extends E, ? extends Number> entry : map.entrySet()) {
			this.size += entry.getValue().intValue();
			this.map.put(entry.getKey(), new Counter(entry.getValue().intValue()));
		}
	}

	public TreeBag(SortedBag<E> bag) {
		Objects.requireNonNull(bag, "Invalid null bag.");

		//TreeMap nao possui um construtor com comparator + map juntos
		if (bag.comparator() != null) {
			map = new TreeMap<>(bag.comparator());
			addAll(bag);
		} else if (bag instanceof AbstractMapBag) {
			size = ((AbstractMapBag)bag).size;
			map = new TreeMap<>(((AbstractMapBag)bag).map);
		} else {
			map = new TreeMap<>();
			addAll(bag);
		}
	}

	@Override
	public Iterator<E> descendingIterator() {
		return ((TreeMap<E, Counter>)map).descendingKeySet().descendingIterator();
	}

	//navigable bag behaviour
	@Override
	public NavigableBag<E> descendingBag() {
		return new TreeBag<>(((TreeMap<E, Counter>)map).descendingMap());
	}

	@Override
	public NavigableBag<E> subBag(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return new TreeBag<>(((TreeMap<E, Counter>)map).subMap(fromElement, fromInclusive, toElement, toInclusive));
	}

	@Override
	public NavigableBag<E> headBag(E toElement, boolean inclusive) {
		return new TreeBag<>(((TreeMap<E, Counter>)map).headMap(toElement, inclusive));
	}

	@Override
	public NavigableBag<E> tailBag(E fromElement, boolean inclusive) {
		return new TreeBag<>(((TreeMap<E, Counter>)map).tailMap(fromElement, inclusive));
	}

	@Override
	public SortedBag<E> subBag(E fromElement, E toElement) {
		return subBag(fromElement, true, toElement, false);
	}

	@Override
	public SortedBag<E> headBag(E toElement) {
		return headBag(toElement, false);
	}

	@Override
	public SortedBag<E> tailBag(E fromElement) {
		return tailBag(fromElement, true);
	}

	@Override
	public Comparator<? super E> comparator() {
		return ((TreeMap<E, Counter>)map).comparator();
	}

	@Override
	public E first() {
		return ((TreeMap<E, Counter>)map).firstKey();
	}

	@Override
	public E last() {
		return ((TreeMap<E, Counter>)map).lastKey();
	}

	@Override
	public E lower(E element) {
		return ((TreeMap<E, Counter>)map).lowerKey(element);
	}

	@Override
	public E floor(E element) {
		return ((TreeMap<E, Counter>)map).floorKey(element);
	}

	@Override
	public E ceiling(E element) {
		return ((TreeMap<E, Counter>)map).ceilingKey(element);
	}

	@Override
	public E higher(E element) {
		return ((TreeMap<E, Counter>)map).higherKey(element);
	}

	@Override
	public E pollFirst() {
		Map.Entry<E, Counter> element = ((TreeMap<E, Counter>)map).pollFirstEntry();
		return (element == null) ? null : element.getKey();
	}

	@Override
	public E pollLast() {
		Map.Entry<E, Counter> element = ((TreeMap<E, Counter>)map).pollLastEntry();
		return (element == null) ? null : element.getKey();
	}

	//cloneable behaviour
	@Override
	public Object clone() {
		try {
			TreeBag<E> clone = (TreeBag<E>)super.clone();
			clone.map = new TreeMap<>(map);
			return clone;
		} catch (CloneNotSupportedException exception) {
			throw new InternalError(exception);
		}
	}

	//object behaviour
	//inherited
}
