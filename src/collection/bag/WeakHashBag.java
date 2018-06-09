//TODO implementar serializable e writeObject()/readObject()
//TODO WeakHashBag has no weak references to its elements, verify WeakReference Entry as WeakHashMap

package collection.bag;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Objects;

public class WeakHashBag<E> extends AbstractMapBag<E> implements Serializable, Cloneable {
	//static state
	private static final long serialVersionUID = -1L;

	//constructors
	public WeakHashBag() {
		map = new WeakHashMap<>();
	}

	public WeakHashBag(int initialCapacity) {
		map = new WeakHashMap<>(initialCapacity);
	}

	public WeakHashBag(int initialCapacity, float loadFactor) {
		map = new WeakHashMap<>(initialCapacity, loadFactor);
	}

	public WeakHashBag(Collection<? extends E> collection) {
		Objects.requireNonNull(collection, "Invalid null collection.");

		if (collection instanceof AbstractMapBag) {
			size = ((AbstractMapBag)collection).size;
			map = new WeakHashMap<>(((AbstractMapBag)collection).map);
		} else {
			map = new WeakHashMap<>(Math.max((int)((collection instanceof Bag ? ((Bag)collection).asEntrySet().size() : collection.size()) / .75f) + 1, 16));
			addAll(collection);
		}
	}

	public WeakHashBag(Map<? extends E, ? extends Number> map) {
		Objects.requireNonNull(map, "Invalid null map.");

		this.map = new WeakHashMap<>(Math.max((int)(map.size() / .75f) + 1, 16));

		for (Map.Entry<? extends E, ? extends Number> entry : map.entrySet()) {
			this.size += entry.getValue().intValue();
			this.map.put(entry.getKey(), new Counter(entry.getValue().intValue()));
		}
	}

	//cloneable behaviour
	@Override
	public Object clone() {
		try {
			WeakHashBag<E> clone = (WeakHashBag<E>)super.clone();
			clone.map = new WeakHashMap<>(map);
			return clone;
		} catch (CloneNotSupportedException exception) {
			throw new InternalError(exception);
		}
	}

	//object behaviour
	//inherited
}
