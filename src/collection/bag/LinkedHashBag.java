package collection.bag;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class LinkedHashBag<E> extends HashBag<E> {
	//static state
	private static final long serialVersionUID = -1L;

	//constructors
	public LinkedHashBag() {
		map = new LinkedHashMap<>();
	}

	public LinkedHashBag(int initialCapacity) {
		map = new LinkedHashMap<>(initialCapacity);
	}

	public LinkedHashBag(int initialCapacity, float loadFactor) {
		map = new LinkedHashMap<>(initialCapacity, loadFactor);
	}

	public LinkedHashBag(Collection<? extends E> collection) {
		Objects.requireNonNull(collection, "Invalid null collection.");

		if (collection instanceof AbstractMapBag) {
			size = ((AbstractMapBag)collection).size;
			map = new LinkedHashMap<>(((AbstractMapBag)collection).map);
		} else {
			map = new LinkedHashMap<>(Math.max((int)((collection instanceof Bag ? ((Bag)collection).asEntrySet().size() : collection.size()) / .75f) + 1, 16));
			addAll(collection);
		}
	}

	public LinkedHashBag(Map<? extends E, ? extends Number> map) {
		Objects.requireNonNull(map, "Invalid null map.");

		this.map = new LinkedHashMap<>(Math.max((int)(map.size() / .75f) + 1, 16));

		for (Map.Entry<? extends E, ? extends Number> entry : map.entrySet()) {
			this.size += entry.getValue().intValue();
			this.map.put(entry.getKey(), new Counter(entry.getValue().intValue()));
		}
	}

	//cloneable behaviour
	@Override
	public Object clone() {
		LinkedHashBag<E> clone = (LinkedHashBag<E>)super.clone();
		clone.map = new LinkedHashMap<>(map);
		return clone;
	}

	//object behaviour
	//inherited
}
