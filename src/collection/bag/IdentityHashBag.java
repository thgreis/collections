//TODO implementar serializable e writeObject()/readObject()

package collection.bag;

import java.io.Serializable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public class IdentityHashBag<E> extends AbstractMapBag<E> implements Serializable, Cloneable {
	//static state
	private static final long serialVersionUID = -1L;

	//constructors
	public IdentityHashBag() {
		map = new IdentityHashMap<>();
	}

	public IdentityHashBag(int expectedMaxSize) {
		map = new IdentityHashMap<>(expectedMaxSize);
	}

	public IdentityHashBag(Collection<? extends E> collection) {
		Objects.requireNonNull(collection, "Invalid null collection.");

		if (collection instanceof AbstractMapBag) {
			size = ((AbstractMapBag)collection).size;
			map = new IdentityHashMap<>(((AbstractMapBag)collection).map);
		} else {
			map = new IdentityHashMap<>(Math.max((int)((collection instanceof Bag ? ((Bag)collection).asEntrySet().size() : collection.size()) / .75f) + 1, 16));
			addAll(collection);
		}
	}

	public IdentityHashBag(Map<? extends E, ? extends Number> map) {
		Objects.requireNonNull(map, "Invalid null map.");

		this.map = new IdentityHashMap<>(Math.max((int)(map.size() / .75f) + 1, 16));

		for (Map.Entry<? extends E, ? extends Number> entry : map.entrySet()) {
			this.size += entry.getValue().intValue();
			this.map.put(entry.getKey(), new Counter(entry.getValue().intValue()));
		}
	}

	//cloneable behaviour
	@Override
	public Object clone() {
		try {
			IdentityHashBag<E> clone = (IdentityHashBag<E>)super.clone();
			clone.map = new IdentityHashMap<>(map);
			return clone;
		} catch (CloneNotSupportedException exception) {
			throw new InternalError(exception);
		}
	}

	//object behaviour
	//inherited
}
