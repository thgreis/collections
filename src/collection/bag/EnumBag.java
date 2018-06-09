//TODO implement constructor for Map<? extends E, ? extends Number> map
//TODO not setting size on constructors
//TODO implementar serializable e writeObject()/readObject()

package collection.bag;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A specialized {@link Bag} implementation for use with enum type elements.
 * All of the elements in an enum bag must come from a single enum type that is
 * specified, explicitly or implicitly, when the bag is created. Enum bags
 * are represented internally as arrays. This representation is extremely
 * compact and efficient.
 * 
 * <p>The iterator returned by the <tt>iterator</tt> method traverses the
 * elements in their <i>natural order</i> (the order in which the enum
 * constants are declared). The returned iterator is <i>weakly
 * consistent</i>: it will never throw {@link java.util.ConcurrentModificationException}
 * and it may or may not show the effects of any modifications to the bag that
 * occur while the iteration is in progress.</p>
 * 
 * <p>Null elements are not permitted. Attempts to insert a null element
 * will throw {@link NullPointerException}. Attempts to test for the
 * presence of a null element or to remove one will, however, function
 * properly.</p>
 * 
 * <p>Like most collection implementations, <tt>EnumBag</tt> is not
 * synchronized. If multiple threads access an enum set concurrently, and at
 * least one of the threads modifies the bag, it should be synchronized
 * externally. This is typically accomplished by synchronizing on some
 * object that naturally encapsulates the enum bag. If no such object exists,
 * the set should be "wrapped" using the <!--{@link Bags#synchronizedBag}-->
 * method. This is best done at creation time, to prevent accidental
 * unsynchronized access:</p>
 * 
 * <pre>
 * Bag&lt;MyEnum&gt; bag = Collections.synchronizedBag(new EnumBag&lt;MyEnum&gt;(...));
 * </pre>
 * 
 * <p>Implementation note: All basic operations execute in constant time.
 * They are likely (though not guaranteed) to be much faster than their
 * {@link HashBag} counterparts. Even bulk operations execute in
 * constant time if their argument is also an enum bag.</p>
 * 
 * @param <E> the type of elements maintained by this bag
 * 
 * @author Thiago Reis
 * @see Bag
 * @see AbstractBag
 * @see java.util.EnumMap
 * @see java.util.EnumSet
 * @since 1.0
 */
public class EnumBag<E> extends AbstractMapBag<E> implements Bag<E>, Serializable, Cloneable {
	//static state
	private static final long serialVersionUID = -1L;

	/**
	 * Creates an empty enum bag with the specified element type.
	 * 
	 * @param type the class object of the element type for this enum bag
	 * @throws NullPointerException if <tt>type</tt> is null
	 */
	public EnumBag(Class<E> type) {
		map = new EnumMap(Objects.requireNonNull(type, "Invalid null type."));
	}

	/**
	 * Creates an enum bag with the same element type as the specified enum
	 * bag, initially containing the same elements (if any).
	 * 
	 * @param bag the enum bag from which to initialize this enum bag
	 * @throws NullPointerException if <tt>bag</tt> is null
	 */
	public EnumBag(EnumBag<E> bag) {
		map = new EnumMap(Objects.requireNonNull(bag, "Invalid null bag.").map);
	}

	/**
	 * Creates an enum bag initialized from the specified bag. If the
	 * specified bag is an <tt>EnumBag</tt> instance, this constructor behaves
	 * identically to {@link #EnumBag(EnumBag)}. Otherwise, the specified bag
	 * must contain at least one element (in order to determine the new
	 * enum bag's element type).
	 * 
	 * @param bag the bag from which to initialize this enum bag
	 * @throws IllegalArgumentException if <tt>bag</tt> is not an
	 * <tt>EnumBag</tt> instance and contains no elements
	 * @throws NullPointerException if <tt>bag</tt> is null
	 */
	public EnumBag(Bag<E> bag) {
		Objects.requireNonNull(bag, "Invalid null bag.");

		if (bag instanceof AbstractMapBag) {
			map = new EnumMap(((AbstractMapBag)bag).map);
		} else {
			map = new EnumMap(bag.toMap(HashMap::new));
		}
	}

	/**
	 * Returns a shallow copy of this enum bag.
	 * (The values themselves are not cloned.)
	 * 
	 * @return a shallow copy of this enum bag
	 */
	@Override
	public Object clone() {
		//TODO estudar e testar clone
		try {
			EnumBag<E> clone = (EnumBag<E>)super.clone();
			clone.map = new EnumMap(map);
			//clone.map = (HashMap<E, Counter>)((HashMap<E, Counter>)map).clone();
			return clone;
		} catch (CloneNotSupportedException exception) {
			throw new InternalError(exception);
		}
	}

	//object behaviour
	//inherited
}
