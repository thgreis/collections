//TODO implementar serializable e writeObject()/readObject()

package collection.bag;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HashBag<E> extends AbstractMapBag<E> implements Bag<E>, Serializable, Cloneable {
	//static state
	private static final long serialVersionUID = -1L;

	//constructors
	public HashBag() {
		map = new HashMap<>();
	}

	public HashBag(int initialCapacity) {
		map = new HashMap<>(initialCapacity);
	}

	public HashBag(int initialCapacity, float loadFactor) {
		map = new HashMap<>(initialCapacity, loadFactor);
	}

	public HashBag(Collection<? extends E> collection) {
		Objects.requireNonNull(collection, "Invalid null collection.");

		if (collection instanceof AbstractMapBag) {
			size = ((AbstractMapBag)collection).size;
			map = new HashMap<>(((AbstractMapBag)collection).map);
		} else {
			map = new HashMap<>(Math.max((int)((collection instanceof Bag ? ((Bag)collection).asEntrySet().size() : collection.size()) / .75f) + 1, 16));
			addAll(collection);
		}
	}

	public HashBag(Map<? extends E, ? extends Number> map) {
		Objects.requireNonNull(map, "Invalid null map.");

		this.map = new HashMap<>(Math.max((int)(map.size() / .75f) + 1, 16));

		for (Map.Entry<? extends E, ? extends Number> entry : map.entrySet()) {
			this.size += entry.getValue().intValue();
			this.map.put(entry.getKey(), new Counter(entry.getValue().intValue()));
		}
	}

	//cloneable behaviour
	@Override
	public Object clone() {
		//TODO estudar e testar clone
		try {
			HashBag<E> clone = (HashBag<E>)super.clone();
			clone.map = new HashMap<>(map);
			//clone.map = (HashMap<E, Counter>)((HashMap<E, Counter>)map).clone();
			return clone;
		} catch (CloneNotSupportedException exception) {
			throw new InternalError(exception);
		}
	}

	//object behaviour
	//inherited
}
