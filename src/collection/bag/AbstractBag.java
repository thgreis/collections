package collection.bag;

import static java.util.Objects.requireNonNull;
import java.io.Serializable;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class provides a skeletal implementation of the <tt>Bag</tt>
 * interface, to minimize the effort required to implement this interface.
 * 
 * <p>To implement an unmodifiable bag, the programmer needs only to extend this
 * class and provide an implementation for the <tt>asElementSet</tt> and
 * <tt>asEntrySet</tt> methods, which return, respectively, a set-view of the
 * bag's elements and entries. Typically, the returned sets will, in turn, be
 * implemented atop <tt>AbstractSet</tt>. This sets should not support the
 * <tt>add</tt> or <tt>remove</tt> methods, and its wrapee should not support
 * the <tt>remove</tt> method.</p>
 * 
 * <p>To implement a modifiable bag, the programmer must additionally override
 * this class's <tt>add</tt>, <tt>put</tt> and <tt>set</tt> methods (which otherwise
 * throws an <tt>UnsupportedOperationException</tt>), and the wrapee returned by
 * <tt>asEntrySet().wrapee()</tt> must additionally implement its
 * <tt>remove</tt> method.</p>
 * 
 * <p>The programmer should generally provide a void (no argument) and bag
 * constructor, as per the recommendation in the <tt>Bag</tt> interface
 * specification.</p>
 * 
 * <p>The documentation for each non-abstract method in this class describes its
 * implementation in detail. Each of these methods may be overridden if the
 * bag being implemented admits a more efficient implementation.</p>
 * 
 * @param <E> the type of elements maintained by this bag
 * 
 * @author Thiago Reis
 * @see Bag
 * @since 1.0
 */
public abstract class AbstractBag<E> extends AbstractCollection<E> implements Bag<E> {
	/**
	 * Sole constructor. (For invocation by subclass constructors, typically implicit.)
	 */
	protected AbstractBag() {
		//empty
	}

	/**
	 * {@inheritDoc}
	 *
	 * implSpec
	 * This implementation iterates over <tt>asEntrySet()</tt> and sums the entry elements counts.
	 * Note that this implementation requires linear time in the size of the
	 * distinct elements of the bag; many implementations will override this method.
	 */
	@Override
	public int size() {
		long size = 0;

		for (Entry<E> entry : asEntrySet()) {
			size += entry.getCount();
		}

		if (size < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		} else if (size > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else {
			return (int)size;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * implSpec
	 * This implementation returns <tt>asEntrySet().isEmpty()</tt>.
	 */
	@Override
	public boolean isEmpty() {
		return asEntrySet().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * implSpec
	 * This implementation returns <tt>count(Object) &gt; 0</tt>.
	 */
	@Override
	public boolean contains(Object object) {
		return count(object) > 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * implSpec
	 * This implementation iterates over <tt>asEntrySet</tt> searching
	 * for an entry with the specified element. If such an entry is found,
	 * the number of occurrences of the specified element is returned.
	 * If the iteration terminates without finding such an entry,
	 * <tt>zero</tt> is returned.
	 * 
	 * Note that this implementation requires linear time in the size of distinct
	 * elements of the bag; many implementations will override this method.
	 *
	 * @throws ClassCastException   {@inheritDoc}
	 * @throws NullPointerException {@inheritDoc}
	 */
	@Override
	public int count(Object object) {
		for (Entry<E> entry : asEntrySet()) {
			if (Objects.equals(object, entry.getElement())) {
				return entry.getCount();
			}
		}

		return 0;
	}

	/**
	 * This field is initialized to contain an instance of the
	 * view the first time this view is requested. The view
	 * is stateless, so there's no reason to create more than one.
	 */
	transient volatile Set<E> elements;
	//transient volatile Set<Entry<E>> entries;

	/**
	 * {@inheritDoc}
	 *
	 * implSpec
	 * This implementation returns a set that subclasses {@link AbstractSet}.
	 * The subclass's iterator method returns a "wrapper object" over this
	 * bag's <tt>asEntrySet()</tt> iterator. The <tt>size</tt> method
	 * delegates to this bag's <tt>size</tt> method and the
	 * <tt>contains</tt> method delegates to this bag's
	 * <tt>contains</tt> method.
	 * 
	 * <p>The set is created the first time this method is called,
	 * and returned in response to all subsequent calls. No synchronization
	 * is performed, so there is a slight chance that multiple calls to this
	 * method will not all return the same set.
	 */
	@Override
	public Set<E> asElementSet() {
		if (elements == null) {
			elements = new AbstractSet<E>() {
				@Override
				public int size() {
					return AbstractBag.this.asEntrySet().size();
				}

				@Override
				public boolean contains(Object object) {
					return AbstractBag.this.contains(object);
				}

				@Override
				public boolean remove(Object object) {
					//removes all occurrences
					return AbstractBag.this.delete(object) != 0;
				}

				@Override
				public void clear() {
					AbstractBag.this.clear();
				}

				@Override
				public Iterator<E> iterator() {
					//asEntrySet().iterator() object wrapper
					return new Iterator<E>() {
						private final Iterator<Entry<E>> wrapee = AbstractBag.this.asEntrySet().iterator();

						@Override
						public boolean hasNext() {
							return wrapee.hasNext();
						}

						@Override
						public E next() {
							return wrapee.next().getElement();
						}

						@Override
						public void remove() {
							wrapee.remove();
						}
					};
				}
			};
		}

		return elements;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This implementation returns a set, created by the provided factory, containing
	 * all the elements returned by this bag's <tt>asEntrySet()</tt> iterator.</p>
	 * 
	 * <p>This method is equivalent to:</p>
	 * 
	 * <pre> {@code
	 * Set<E> set = factory.get();
	 * for (Entry<E> entry : asEntrySet()) {
	 *     set.add(entry.getElement());
	 * }
	 * return set;
	 * }</pre>
	 */
	@Override
	public Set<E> toSet(Supplier<Set<E>> factory) {
		//shallow copy
		Set<E> result = requireNonNull(factory, "Invalid null factory.").get();

		for (Entry<E> entry : asEntrySet()) {
			result.add(entry.getElement());
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This implementation returns a map, created by the provided factory, containing
	 * all the elements returned by this bag's <tt>asEntrySet()</tt> iterator.
	 * 
	 * <p>This method is equivalent to:</p>
	 * 
	 * <pre> {@code
	 * Map<E, Integer> map = factory.get();
	 * for (Entry<E> entry : asEntrySet()) {
	 *     map.put(entry.getElement(), entry.getCount());
	 * }
	 * return map;
	 * }</pre>
	 */
	@Override
	public Map<E, Integer> toMap(Supplier<Map<E, Integer>> factory) {
		//shallow copy
		Map<E, Integer> result = requireNonNull(factory, "Invalid null factory.").get();

		for (Entry<E> entry : asEntrySet()) {
			result.put(entry.getElement(), entry.getCount());
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 *
	 * implSpec
	 * This implementation calls <tt>put(element, 1)</tt>.
	 */
	@Override
	public boolean add(E element) {
		put(element, 1);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @param  element                       {@inheritDoc}
	 * @param  amount                        {@inheritDoc}
	 * @return                               {@inheritDoc}
	 * @throws UnsupportedOperationException {@inheritDoc}
	 * @throws ClassCastException            {@inheritDoc}
	 * @throws NullPointerException          {@inheritDoc}
	 * @throws IllegalArgumentException      {@inheritDoc}
	 * @throws IllegalStateException         {@inheritDoc}
	 */
	@Override
	public int put(E element, int amount) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 *
	 * @param  element                       {@inheritDoc}
	 * @param  count                         {@inheritDoc}
	 * @return                               {@inheritDoc}
	 * @throws UnsupportedOperationException {@inheritDoc}
	 * @throws ClassCastException            {@inheritDoc}
	 * @throws NullPointerException          {@inheritDoc}
	 * @throws IllegalArgumentException      {@inheritDoc}
	 * @throws IllegalStateException         {@inheritDoc}
	 */
	@Override
	public int set(E element, int count) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This implementation iterates over the bag's <tt>asEntrySet</tt> view
	 * looking for the specified element. If it finds the element, a single
	 * occurrence is removed from this bag with the {@link Bag.Entry}
	 * <tt>setCount()</tt> method or by iterator's <tt>remove</tt> method.</p>
	 * 
	 * <p>Note that this implementation throws an <tt>UnsupportedOperationException</tt>
	 * if the iterator returned by this bag's <tt>asEntrySet</tt> view's iterator
	 * method does not implement the <tt>remove</tt> method and this bag contains
	 * the specified object.</p>
	 *
	 * @throws UnsupportedOperationException {@inheritDoc}
	 * @throws ClassCastException            {@inheritDoc}
	 * @throws NullPointerException          {@inheritDoc}
	 */
	@Override
	public boolean remove(Object object) {
		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			Entry<E> entry = iterator.next();

			if (Objects.equals(object, entry.getElement())) {
				if (entry.getCount() - 1 > 0) {
					entry.setCount(entry.getCount() - 1);
				} else {
					iterator.remove();
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation iterates over the bag's <tt>asEntrySet</tt> view
	 * looking for the specified element. If it finds the element, all of its
	 * occurrences are removed from this bag with the iterator's <tt>remove</tt>
	 * method.</p>
	 * 
	 * <p>Note that this implementation throws an <tt>UnsupportedOperationException</tt>
	 * if the iterator returned by this bag's <tt>asEntrySet</tt> view's iterator
	 * method does not implement the <tt>remove</tt> method and this bag contains
	 * the specified object.</p>
	 * 
	 * @throws UnsupportedOperationException {@inheritDoc}
	 * @throws ClassCastException            {@inheritDoc}
	 * @throws NullPointerException          {@inheritDoc}
	 */
	@Override
	public int delete(Object object) {
		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			Entry<E> entry = iterator.next();

			if (Objects.equals(object, entry.getElement())) {
				iterator.remove();
				return entry.getCount();
			}
		}

		return 0;
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		requireNonNull(collection, "Invalid null collection.");

		Bag<?> surrogate = (collection instanceof Bag ? (Bag<?>)collection : new HashBag<>(collection));

		if (surrogate.asEntrySet().size() > asEntrySet().size()) {
			return false;
		}

		//specified collection is smaller than or equals to this bag
		for (Entry<?> entry : surrogate.asEntrySet()) {
			if (entry.getCount() > count(entry.getElement())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		requireNonNull(collection, "Invalid null collection.");

		boolean result = false;

		if (collection instanceof Bag) {
			for (Entry<? extends E> entry : ((Bag<? extends E>)collection).asEntrySet()) {
				put(entry.getElement(), entry.getCount());
				result = true;
			}
		} else {
			for (E element : collection) {
				add(element);
				result = true;
			}
		}

		return result;
	}

	/**
	 * Removes from this bag all of its element occurrences that are contained
	 * in the specified collection (optional operation). If the specified
	 * collection is also a bag, this operation effectively modifies this
	 * bag so that its count is the <i>asymmetric set difference</i> of
	 * the two bags.
	 * 
     * <p>If the specified collection is not another bag, this implementation
	 * determines which is the smaller of this bag and the specified collection,
	 * by invoking the <tt>size</tt> method on each. If this bag has fewer elements,
	 * then the implementation iterates over this bag, checking each element occurrence
     * returned by the iterator in turn to see if it is contained in the specified
	 * collection. If it is so contained, it is removed from this bag with the
	 * iterator's <tt>remove</tt> method. If the specified collection has fewer
	 * elements, then the implementation iterates over the specified collection,
	 * removing from this bag each element occurrence returned by the iterator,
	 * using this bag's <tt>remove</tt> method.</p>
	 * 
	 * <p>If the specified collection is also a bag, this implementation
	 * determines which is the smaller of this bag and the specified bag,
	 * by invoking the <tt>asEntrySet</tt> size method on each. If this
	 * bag has fewer elements, then the implementation iterates over this
	 * <tt>asEntrySet</tt> view, checking each entry returned by the
	 * iterator in turn to see if its occurrence count is contained in
	 * the specified bag. If it is so contained, its occurrences are
	 * removed from this bag with the {@link Bag.Entry} <tt>setCount()</tt>
	 * method or by iterator's <tt>remove</tt> method. If the
	 * specified bag has fewer elements, then the implementation iterates
	 * over the specified bag's <tt>asEntrySet</tt> view, removing from
	 * this bag each element occurrence count returned by the iterator,
	 * using this bag's <tt>putIf</tt> method with a negative amount.
	 * 
	 * <p>Note that this implementation will throw an
	 * <tt>UnsupportedOperationException</tt> if the iterator returned by the
	 * <tt>iterator</tt> method does not implement the <tt>remove</tt> method.
	 * 
	 * @param  collection collection containing elements to be removed from this bag
	 * @return <tt>true</tt> if this bag changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation
	 *         is not supported by this bag
	 * @throws ClassCastException if the class of an element of this bag
	 *         is incompatible with the specified collection
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if this bag contains a null element and the
	 *         specified collection does not permit null elements
	 *         (<a href="Collection.html#optional-restrictions">optional</a>),
	 *         or if the specified collection is null
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override
	public boolean removeAll(Collection<?> collection) {
		requireNonNull(collection, "Invalid null collection.");

		boolean result = false;

		if (collection instanceof Bag) {
			Bag smaller = (asEntrySet().size() <= ((Bag<?>)collection).asEntrySet().size() ? this : (Bag<?>)collection);
			Bag bigger = (asEntrySet().size() <= ((Bag<?>)collection).asEntrySet().size() ? (Bag<?>)collection : this);

			for (Iterator<Entry<?>> iterator = smaller.asEntrySet().iterator(); iterator.hasNext();) {
				Entry<?> entry = iterator.next();

				if (smaller == this) {
					int count = bigger.count(entry.getElement());

					if (count > 0) {
						if (entry.getCount() - count > 0) {
							entry.setCount(entry.getCount() - count);
						} else {
							iterator.remove();
						}

						result = true;
					}
				} else {
					result |= putIf(element -> Objects.equals(element, entry.getElement()), -entry.getCount());
				}
			}
		} else {
			for (Object element : collection) {
				result |= remove(element);
			}

			//TODO fix algorithm
			/*
			if (size() <= collection.size()) {
				for (Iterator<E> iterator = iterator(); iterator.hasNext();) {
					//correct collection.contains()
					if (collection.contains(iterator.next())) {
						iterator.remove();
						result = true;
					}
				}
			} else {
				for (Object element : collection) {
					result |= remove(element);
				}
			}
			*/
		}

		return result;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		requireNonNull(collection, "Invalid null collection.");

		boolean result = false;
		Bag<?> surrogate = (collection instanceof Bag ? (Bag<?>)collection : new HashBag<>(collection));

		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			Entry<E> entry = iterator.next();
			int count = surrogate.count(entry.getElement());

			if (count == 0) {
				iterator.remove();
				result = true;
			} else if (entry.getCount() > count) {
				entry.setCount(count);
				result = true;
			}
		}

		return result;
	}

	@Override
	public boolean addIf(Predicate<? super E> filter) {
		return putIf(filter, 1);
	}

	@Override
	public boolean putIf(Predicate<? super E> filter, int amount) {
		requireNonNull(filter, "Invalid null filter.");

		if (amount == 0) {
			return false;
		}

		boolean result = false;

		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			Entry<E> entry = iterator.next();

			if (filter.test(entry.getElement())) {
				if (entry.getCount() + amount > 0) {
					entry.setCount(entry.getCount() + amount);
				} else {
					iterator.remove();
				}

				result = true;
			}
		}

		return result;
	}

	@Override
	public boolean setIf(Predicate<? super E> filter, int count) {
		requireNonNull(filter, "Invalid null filter.");

		boolean result = false;

		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			Entry<E> entry = iterator.next();

			if (filter.test(entry.getElement())) {
				if (entry.getCount() != count) {
					if (count > 0) {
						entry.setCount(count);
					} else {
						iterator.remove();
					}

					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * implSpec
	 * This implementation traverses all distinct elements of the bag using
	 * bag's <tt>asEntrySet</tt> view's iterator. Each matching element is
	 * removed using iterator's <tt>remove</tt> method. If the bag's
	 * <tt>asEntrySet</tt> view's iterator does not support removal then an
	 * {@code UnsupportedOperationException} will be thrown on the first
	 * matching element.
	 */
	@Override
	public boolean removeIf(Predicate<? super E> filter) {
		requireNonNull(filter, "Invalid null filter.");

		boolean result = false;

		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			if (filter.test(iterator.next().getElement())) {
				iterator.remove();
				result = true;
			}
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>This implementation iterates over this bag's <tt>asEntrySet</tt> view,
	 * removing each element using the <tt>Iterator.remove</tt> operation. Most
	 * implementations will probably choose to override this method for efficiency.</p>
	 * 
	 * <p>Note that this implementation will throw an
	 * <tt>UnsupportedOperationException</tt> if the iterator returned by this
	 * bag's <tt>asEntrySet</tt> view's <tt>iterator</tt> method does not implement the
	 * <tt>remove</tt> method and this bag is non-empty.</p>
	 * 
	 * @throws UnsupportedOperationException {@inheritDoc}
	 */
	@Override
	public void clear() {
		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			iterator.next();
			iterator.remove();
		}
	}

	/**
	 * Returns the hash code value for this bag. The hash code of a bag is
	 * defined to be the sum of the hash codes of each entry in the bag's
	 * <tt>asEntrySet()</tt> view. This ensures that <tt>b1.equals(b2)</tt>
	 * implies that <tt>b1.hashCode()==b2.hashCode()</tt> for any two bags
	 * <tt>b1</tt> and <tt>b2</tt>, as required by the general contract of
	 * {@link Object#hashCode}.
	 * 
	 * @implSpec
	 * This implementation iterates over <tt>asEntrySet()</tt>, calling
	 * {@link Bag.Entry#hashCode hashCode()} on each element (entry) in the
	 * set, and adding up the results.
	 *
	 * @return the hash code value for this bag
	 * @see Bag.Entry#hashCode()
	 * @see Object#equals(Object)
	 * @see Set#equals(Object)
	 */
	@Override
	public int hashCode() {
		//works as a map
		int hashcode = 0;

		for (Entry<E> entry : asEntrySet()) {
			hashcode += entry.hashCode();
		}

		return hashcode;
	}

	/**
	 * Compares the specified object with this bag for equality. Returns
	 * <tt>true</tt> if the given object is also a bag, the two bags have
	 * the same size (number of distinct elements and number of element
	 * occurrences), and every member of the given bag is contained in
	 * this bag. This ensures that the <tt>equals</tt> method works
	 * properly across different implementations of the <tt>Bag</tt>
	 * interface.
	 * 
	 * This implementation first checks if the specified object is this
	 * bag; if so it returns <tt>true</tt>. Then, it checks if the
	 * specified object is a bag whose size is identical to the size of
	 * this bag (number of distinct elements and number of element occurrences);
	 * if not, it returns <tt>false</tt>. If so, it iterates over the bag's
	 * <tt>asEntrySet</tt> view and compares the elements occurrences count
	 * of both bags. If any of the element occurrences counts do not match,
	 * then it returns <tt>false</tt>, otherwise <tt>true</tt>.
	 * 
	 * @param  object object to be compared for equality with this bag
	 * @return <tt>true</tt> if the specified object is equal to this bag
	 */
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}

		if (!(object instanceof Bag)) {
			return false;
		}

		Bag<?> surrogate = (Bag<?>)object;

		if (size() != surrogate.size() || asEntrySet().size() != surrogate.asEntrySet().size()) {
			return false;
		}

		//exactly the same number of elements and element occurrences in both bags, so comparison is valid
		for (Entry<E> entry : asEntrySet()) {
			if (surrogate.count(entry.getElement()) != entry.getCount()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * implSpec
	 * This implementation iterates over <tt>asEntrySet()</tt>
	 * to build the string representation.
	 */
	@Override
	public String toString() {
		if (isEmpty()) {
			return "[]";
		}

		StringBuilder result = new StringBuilder();
		result.append('[');

		for (Iterator<Entry<E>> iterator = asEntrySet().iterator(); iterator.hasNext();) {
			Entry<E> entry = iterator.next();

			result
				.append(entry.getElement() == this ? "(this Bag)" : String.valueOf(entry.getElement()))
				.append('=')
				.append(entry.getCount());

			if (iterator.hasNext()) {
				result
					.append(',')
					.append(' ');
			}
		}

		return result.append(']').toString();
	}

	/**
	 * An Entry maintaining an element and a count. The count may be
	 * changed using the <tt>setCount</tt> method. This class facilitates
	 * the process of building custom bag implementations. For example,
	 * it may be convenient to return arrays of <tt>SimpleEntry</tt>
	 * instances in method <tt>Bag.asEntrySet().toArray</tt>.
	 * 
	 * @param <E> the type of elements maintained by this entry
	 * @since 1.0
	 */
	public static class SimpleEntry<E> implements Entry<E>, Serializable {
		//static state
		private static final long serialVersionUID = -1L;

		//immutable state
		private final E element;

		//mutable state
		private int count;

		/**
		 * Creates an entry representing a pair of element-count.
		 * 
		 * @param element the element represented by this entry
		 * @param count the count represented by this entry
		 */
		public SimpleEntry(E element, int count) {
			this.element = element;
			this.count = count;
		}

		/**
		 * Creates an entry representing the same
		 * element-count pair as the specified entry.
		 * 
		 * @param entry the entry to copy
		 */
		public SimpleEntry(Entry<? extends E> entry) {
			requireNonNull(entry, "Invalid null entry.");
			this.element = entry.getElement();
			this.count = entry.getCount();
		}

		/**
		 * Returns the element corresponding to this entry.
		 * 
		 * @return the element corresponding to this entry
		 */
		@Override
		public E getElement() {
			return element;
		}

		/**
		 * Returns the count corresponding to this entry.
		 * 
		 * @return the count corresponding to this entry
		 */
		@Override
		public int getCount() {
			return count;
		}

		/**
		 * Replaces the count corresponding to
		 * this entry with the specified count.
		 *
		 * @param  count new count to be stored in this entry
		 * @return the old count corresponding to the entry
		 */
		@Override
		public int setCount(int count) {
			if (count < 1) {
				throw new IllegalArgumentException("Invalid count value for entry: " + toString() + ".");
			}

			int result = count;
			this.count = count;
			return result;
		}

		/**
		 * Returns the hash code value for this bag entry.
		 * The hash code of a bag entry {@code e} is defined to be:
		 * <pre>
		 *   (e.getElement()==null ? 0 : e.getElement().hashCode()) ^
		 *   (e.getCount()==null ? 0 : e.getCount().hashCode())
		 * </pre>
		 * This ensures that {@code e1.equals(e2)} implies that
		 * {@code e1.hashCode()==e2.hashCode()} for any two Entries
		 * {@code e1} and {@code e2}, as required by the general
		 * contract of {@link Object#hashCode}.
		 *
		 * @return the hash code value for this bag entry
		 * @see    #equals
		 */
		@Override
		public int hashCode() {
			return Objects.hashCode(element) ^ Objects.hashCode(count);
		}

		/**
		 * Compares the specified object with this entry for equality.
		 * Returns {@code true} if the given object is also a bag entry and
		 * the two entries represent the same element-count pair. More
		 * formally, two entries {@code e1} and {@code e2} represent the
		 * same element-count pair if
		 * <pre>
		 *   (e1.getElement()==null ?
		 *    e2.getElement()==null :
		 *    e1.getElement().equals(e2.getElement()))
		 *   &amp;&amp;
		 *   (e1.getCount()==null ?
		 *    e2.getCount()==null :
		 *    e1.getCount().equals(e2.getCount()))
		 * </pre>
		 * This ensures that the {@code equals} method works properly across
		 * different implementations of the {@code Bag.Entry} interface.
		 *
		 * @param  object object to be compared for equality with this bag entry
		 * @return {@code true} if the specified object is equal to this bag entry
		 * @see    #hashCode
		 */
		@Override
		public boolean equals(Object object) {
			return object == this || (
				object instanceof Entry
				&& Objects.equals(element, ((Entry<?>)object).getElement())
				&& count == ((Entry<?>)object).getCount()
				//&& Objects.equals(count, ((Entry<?>)object).getCount())
			);
		}

		/**
		 * Returns a String representation of this bag entry. This
		 * implementation returns the string representation of this
		 * entry's element followed by the equals character ("<tt>=</tt>")
		 * followed by the string representation of this entry's count.
		 * 
		 * @return a String representation of this bag entry
		 */
		@Override
		public String toString() {
			return element + "=" + count;
		}
	}

	/**
	 * An Entry maintaining an immutable element and a count.
	 * This class does not support method <tt>setCount</tt>. This
	 * class may be convenient in methods that return thread-safe
	 * snapshots of element-count pairs.
	 * 
	 * @param <E> the type of elements maintained by this entry
	 * @since 1.0
	 */
	public static class SimpleImmutableEntry<E> implements Entry<E>, Serializable {
		//static state
		private static final long serialVersionUID = -1L;

		//immutable state
		private final E element;
		private final int count;

		/**
		 * Creates an entry representing a pair of element-count.
		 * 
		 * @param element the element represented by this entry
		 * @param count the count represented by this entry
		 */
		public SimpleImmutableEntry(E element, int count) {
			this.element = element;
			this.count = count;
		}

		/**
		 * Creates an entry representing the same
		 * element-count pair as the specified entry.
		 * 
		 * @param entry the entry to copy
		 */
		public SimpleImmutableEntry(Entry<? extends E> entry) {
			requireNonNull(entry, "Invalid null entry.");
			this.element = entry.getElement();
			this.count = entry.getCount();
		}

		/**
		 * Returns the element corresponding to this entry.
		 * 
		 * @return the element corresponding to this entry
		 */
		@Override
		public E getElement() {
			return element;
		}

		/**
		 * Returns the count corresponding to this entry.
		 * 
		 * @return the count corresponding to this entry
		 */
		@Override
		public int getCount() {
			return count;
		}

		/**
		 * Replaces the count corresponding to this entry with the specified
		 * count (optional operation). This implementation simply throws
		 * <tt>UnsupportedOperationException</tt>, as this class implements
		 * an <i>immutable</i> bag entry.
		 * 
		 * @param  count new count to be stored in this entry
		 * @return (does not return)
		 * @throws UnsupportedOperationException always
		 */
		@Override
		public int setCount(int count) {
			throw new UnsupportedOperationException("Invalid operation on immutable object.");
		}

		/**
		 * Returns the hash code value for this bag entry.
		 * The hash code of a bag entry {@code e} is defined to be:
		 * <pre>
		 *   (e.getElement()==null ? 0 : e.getElement().hashCode()) ^
		 *   (e.getCount()==null ? 0 : e.getCount().hashCode())
		 * </pre>
		 * This ensures that {@code e1.equals(e2)} implies that
		 * {@code e1.hashCode()==e2.hashCode()} for any two Entries
		 * {@code e1} and {@code e2}, as required by the general
		 * contract of {@link Object#hashCode}.
		 *
		 * @return the hash code value for this bag entry
		 * @see    #equals
		 */
		@Override
		public int hashCode() {
			return Objects.hashCode(element) ^ Objects.hashCode(count);
		}

		/**
		 * Compares the specified object with this entry for equality.
		 * Returns {@code true} if the given object is also a bag entry and
		 * the two entries represent the same element-count pair. More
		 * formally, two entries {@code e1} and {@code e2} represent the
		 * same element-count pair if
		 * <pre>
		 *   (e1.getElement()==null ?
		 *    e2.getElement()==null :
		 *    e1.getElement().equals(e2.getElement()))
		 *   &amp;&amp;
		 *   (e1.getCount()==null ?
		 *    e2.getCount()==null :
		 *    e1.getCount().equals(e2.getCount()))
		 * </pre>
		 * This ensures that the {@code equals} method works properly across
		 * different implementations of the {@code Bag.Entry} interface.
		 *
		 * @param  object object to be compared for equality with this bag entry
		 * @return {@code true} if the specified object is equal to this bag entry
		 * @see    #hashCode
		 */
		@Override
		public boolean equals(Object object) {
			return object == this || (
				object instanceof Entry
				&& Objects.equals(element, ((Entry<?>)object).getElement())
				&& count == ((Entry<?>)object).getCount()
				//&& Objects.equals(count, ((Entry<?>)object).getCount())
			);
		}

		/**
		 * Returns a String representation of this bag entry. This
		 * implementation returns the string representation of this
		 * entry's element followed by the equals character ("<tt>=</tt>")
		 * followed by the string representation of this entry's count.
		 * 
		 * @return a String representation of this bag entry
		 */
		@Override
		public String toString() {
			return element + "=" + count;
		}
	}
}
