package collection.bag;

import static java.util.Objects.requireNonNull;
import collection.AbstractIterator;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class provides a map-based implementation of the <tt>Bag</tt>
 * interface, to minimize the effort required to implement this interface.
 * 
 * @param <E> the type of elements maintained by this bag
 * 
 * @author Thiago Reis
 * @see Bag
 * @see AbstractBag
 * @since 1.0
 */
abstract class AbstractMapBag<E> extends AbstractBag<E> {
	protected transient Map<E, Counter> map;

	/**
	 * The number of element occurrences contained in this bag.
	 */
	protected transient long size = 0;

	/**
	 * The number of times this bag has been structurally modified.
	 * Structural modifications are those that change the number of occurrences
	 * in the bag or otherwise modify its internal structure (e.g., rehash).
	 * This field is used to make iterators on Collection-views of
	 * the bag fail-fast. (See ConcurrentModificationException).
	 */
	protected transient int modification = 0;

	/**
	 * Sole constructor. (For invocation by subclass constructors, typically implicit.)
	 */
	protected AbstractMapBag() {
		//empty
	}

	@Override
	public int size() {
		if (size < Integer.MIN_VALUE) {
			return Integer.MIN_VALUE;
		}

		if (size > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}

		return (int)size;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object object) {
		return map.containsKey(object);
	}

	@Override
	public int count(Object object) {
		Counter counter = map.get(object);
		return (counter == null ? 0 : counter.getCount());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * implSpec
	 * This implementation returns a "wrapper object" over this
	 * bag's <tt>asEntrySet()</tt> iterator whose methods delegate
	 * to <tt>asEntrySet()</tt> iterator and operate on multiples
	 * occurrences of the bag's elements.
	 */
	@Override
	public Iterator<E> iterator() {
		//mapper.entrySet().iterator() object wrapper and interface adapter
		return new AbstractIterator<E>() {
			//immutable state
			private final Iterator<Map.Entry<E, Counter>> wrapee = map.entrySet().iterator();

			//mutable state
			private Map.Entry<E, Counter> current;
			private int countdown = 0;

			//initializer
			{
				setRemovable(false);
				setModification(modification);
			}

			//iterator behaviour
			@Override
			public boolean hasNext() {
				return wrapee.hasNext() || countdown > 0;
			}

			@Override
			public E next() {
				checkNext(hasNext());
				checkModification(modification);
				setRemovable(true);

				if (countdown == 0) {
					current = wrapee.next();
					countdown = current.getValue().getCount();
				}

				countdown--;
				return current.getKey();
			}

			@Override
			public void remove() {
				checkRemovable();
				checkModification(modification);
				setRemovable(false);

				if (current.getValue().getCount() > 1) {
					countdown--;
					current.getValue().setCount(current.getValue().getCount() - 1);
				} else {
					wrapee.remove();
				}

				setModification(modify(-current.getValue().getCount()));
			}
		};
	}

	/**
	 * This field is initialized to contain an instance of the
	 * view the first time this view is requested. The view
	 * is stateless, so there's no reason to create more than one.
	 */
	protected transient volatile Set<Entry<E>> entries;

	@Override
	public Set<Entry<E>> asEntrySet() {
		if (entries == null) {
			entries = new AbstractSet<Entry<E>>() {
				@Override
				public int size() {
					return map.size();
				}

				@Override
				public boolean contains(Object object) {
					if (!(object instanceof Entry)) {
						return false;
					}

					/*
					return map.entrySet().contains(
						new AbstractMap.SimpleEntry(
							((Entry<?>)object).getElement(),
							new Counter(((Entry<?>)object).getCount())
						)
					);
					*/

					return map.get(((Entry<?>)object).getElement()).getCount() == ((Entry<?>)object).getCount();
				}

				@Override
				public boolean remove(Object object) {
					if (!(object instanceof Entry)) {
						return false;
					}

					/*
					return map.entrySet().remove(
						new AbstractMap.SimpleEntry(
							((Entry<?>)object).getElement(),
							new Counter(((Entry<?>)object).getCount())
						)
					);
					*/

					/*
					Counter counter = map.get(((Entry<?>)object).getElement());

					if (counter == null || counter.getCount() != ((Entry<?>)object).getCount()) {
						return false;
					}

					remove(((Entry<?>)object).getElement());
					return true;
					*/

					if (map.remove(((Entry<?>)object).getElement(), new Counter(((Entry<?>)object).getCount()))) {
						modify(-((Entry<?>)object).getCount());
						return true;
					}

					return false;
				}

				@Override
				public void clear() {
					AbstractMapBag.this.clear();
				}

				@Override
				public Iterator<Entry<E>> iterator() {
					//mapper.entrySet().iterator() object wrapper and interface adapter
					return new AbstractIterator<Entry<E>>() {
						//immutable state
						private final Iterator<Map.Entry<E, Counter>> wrapee = map.entrySet().iterator();

						//mutable state
						private Map.Entry<E, Counter> current;

						//initializer
						{
							setRemovable(false);
							setModification(modification);
						}

						//iterator behaviour
						@Override
						public boolean hasNext() {
							return wrapee.hasNext();
						}

						@Override
						public Entry<E> next() {
							checkNext(hasNext());
							checkModification(modification);
							setRemovable(true);
							current = wrapee.next();

							return new Entry<E>() {
								//immutable state
								private final Map.Entry<E, Counter> entry = current;

								//entry behaviour
								@Override
								public E getElement() {
									return entry.getKey();
								}

								@Override
								public int getCount() {
									return entry.getValue().getCount();
								}

								@Override
								public int setCount(int count) {
									if (count < 1) {
										throw new IllegalArgumentException("Invalid count value for entry: " + toString() + ".");
									}

									int result = entry.getValue().setCount(count);
									setModification(modify(count - result));
									return result;
								}

								//object behaviour
								@Override
								public int hashCode() {
									//return Arrays.hashCode(entry.getKey(), entry.getValue());

									/*
									int result = 17;
									result = 31 * result + entry.getKey().hashCode();
									result = 31 * result + entry.getValue().hashCode();
									return result;
									*/

									/*
									int result = Objects.hashCode(entry.getKey());

									for (int occurrence = 1; occurrence < entry.getValue().getCount(); occurrence++) {
										result += result;
									}

									return result;
									*/

									return Objects.hashCode(entry.getKey()) ^ Objects.hashCode(entry.getValue());
								}

								@Override
								public boolean equals(Object object) {
									return object == this || (
										object instanceof Entry
										&& Objects.equals(entry.getKey(), ((Entry<E>)object).getElement())
										&& entry.getValue().getCount() == ((Entry<E>)object).getCount()
										//&& Objects.equals(entry.getValue().getCount(), ((Entry<E>)object).getCount())
									);
								}

								@Override
								public String toString() {
									return entry.getKey() + "=" + entry.getValue();
								}
							};
						}

						@Override
						public void remove() {
							checkRemovable();
							checkModification(modification);
							setRemovable(false);
							wrapee.remove();
							setModification(modify(-current.getValue().getCount()));
						}
					};
				}
			};
		}

		return entries;
	}

	@Override
	public int put(E element, int amount) {
		//if absent, do insert
		if (amount == 0 ) {
			Counter oldCounter = map.get(element);
			return (oldCounter == null ? 0 : oldCounter.getCount());
		} else if (amount > 0 ) {
			Counter newCounter = new Counter(amount);
			Counter oldCounter = map.put(element, newCounter);

			if (oldCounter != null) {
				newCounter.setCount(oldCounter.getCount() + amount);
				modify(amount);
				return oldCounter.getCount();
			} else {
				modify(amount);
				return 0;
			}
		} else {
			Counter oldCounter = map.get(element);

			if (oldCounter != null) {
				if (oldCounter.getCount() + amount > 0) {
					int value = oldCounter.getCount();
					oldCounter.setCount(value + amount);
					modify(amount);
					return value;
				} else {
					map.remove(element);
					modify(-oldCounter.getCount());
					return oldCounter.getCount();
				}
			} else {
				return 0;
			}
		}
	}

	@Override
	public int set(E element, int count) {
		//change getCount directly (increase, decrease or sets to zero)
		//if absent, do insert

		if (count > 0) {
			Counter oldCounter = map.put(element, new Counter(count));
			modify(count - (oldCounter == null ? 0 : oldCounter.getCount()));
			return (oldCounter == null ? 0 : oldCounter.getCount());
		} else {
			Counter oldCounter = map.remove(element);

			if (oldCounter != null) {
				modify(-oldCounter.getCount());
				return oldCounter.getCount();
			} else {
				return 0;
			}
		}
	}

	@Override
	public boolean remove(Object object) {
		//if absent, do not insert
		Counter oldCounter = map.get(object);

		if (oldCounter != null) {
			if (oldCounter.getCount() - 1 > 0) {
				oldCounter.setCount(oldCounter.getCount() - 1);
				modify(-1);
				return true;
			} else {
				map.remove(object);
				modify(-oldCounter.getCount());
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public int delete(Object object) {
		//change getCount directly (sets it to zero and removes)
		Counter oldCounter = map.remove(object);

		if (oldCounter != null) {
			modify(-oldCounter.getCount());
			return oldCounter.getCount();
		} else {
			return 0;
		}
	}

	@Override
	public void clear() {
		map.clear();
		modify(-size());
	}

	//object behaviour
	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec
	 * This implementation calls {@link Map#hashCode hashCode()} method of the backing map.
	 */
	@Override
	public int hashCode() {
		return map.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}

		if (!(object instanceof Bag)) {
			return false;
		}

		Bag<?> surrogate = (Bag<?>)object;

		if (surrogate instanceof AbstractMapBag) {
			if (surrogate.size() != size() || surrogate.asEntrySet().size() != asEntrySet().size()) {
				return false;
			}

			return map.equals(((AbstractMapBag)surrogate).map);
		} else {
			return super.equals(object);
		}
	}

	@Override
	public String toString() {
		return map.toString();
	}

	//miscellaneous
	protected final int modify(int amount) {
		size += amount;
		assert size >= 0 : "Invalid bag negative size.";
		return ++modification;
	}

	final static class Counter /*extends Number*/ implements Comparable<Counter>, Serializable {
		//static state
		private static final long serialVersionUID = -1L;

		//mutable state
		private int count;

		//constructor
		public Counter(int count) {
			assert count > 0;
			this.count = count;
		}

		//counter behaviour
		public int getCount() {
			return count;
		}

		public int setCount(int count) {
			assert count > 0;
			int result = this.count;
			this.count = count;
			return result;
		}

		//comparable behaviour
		@Override
		public int compareTo(Counter counter) {
			return Integer.compare(count, requireNonNull(counter, "Invalid null counter.").count);
		}

		//object behaviour
		@Override
		public int hashCode() {
			return count;
		}

		@Override
		public boolean equals(Object object) {
			return object == this || (object instanceof Counter && Integer.compare(((Counter)object).count, count) == 0);
		}

		@Override
		public String toString() {
			return Integer.toString(count);
		}
	}
}
