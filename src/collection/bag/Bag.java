//TODO compare with another bags implementations
//TODO make all javadoc
//TODO make all maps types from collections framework (ConcurrentHashBag, ConcurrentSkipListBag...)
//TODO change comments to english
//TODO add todo comments on test cases
//TODO add assertions to check conditions
//TODO add countIterator (an iterator that iterates asc/desc according to the order of elements count)

//TODO adjust "see Bags#singleton(java.lang.Object)" and "see Bags#EMPTY_BAG" comments

package collection.bag;

import java.io.Serializable;
import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.Objects;

/**
 * A collection that can contains duplicate elements. In other words, a bag
 * can contain multiple element occurrences instead of single element
 * occurrence as in <tt>Sets</tt>. More formally, bags can contain pair of
 * elements <code>e1</code> and <code>e2</code> such that
 * <code>e1.equals(e2)</code>, and multiple null elements. This interface
 * models the mathematical <i>multiset</i> abstraction.
 * 
 * <p>The <tt>Bag</tt> interface places additional stipulations, beyond those
 * inherited from the <tt>Collection</tt> interface, on the contracts of all
 * constructors and on the contracts of the <tt>add</tt>, <tt>equals</tt> and
 * <tt>hashCode</tt> methods. Declarations for other inherited methods are
 * also included here for convenience. (The specifications accompanying these
 * declarations have been tailored to the <tt>Bag</tt> interface, but they do
 * not contain any additional stipulations.)</p>
 * 
 * <p>Note: Great care must be exercised if mutable objects are used as bag
 * elements. The behavior of a bag is not specified if the value of an object
 * is changed in a manner that affects <tt>equals</tt> comparisons while the
 * object is an element in the bag. A special case of this prohibition is
 * that it is not permissible for a bag to contain itself as an element.</p>
 * 
 * <p>Some bag implementations have restrictions on the elements that
 * they may contain. For example, some implementations prohibit null elements,
 * and some have restrictions on the types of their elements. Attempting to
 * add an ineligible element throws an unchecked exception, typically
 * <tt>NullPointerException</tt> or <tt>ClassCastException</tt>. Attempting
 * to query the presence of an ineligible element may throw an exception,
 * or it may simply return false; some implementations will exhibit the former
 * behavior and some will exhibit the latter. More generally, attempting an
 * operation on an ineligible element whose completion would not result in
 * the insertion of an ineligible element into the bag may throw an
 * exception or it may succeed, at the option of the implementation.
 * Such exceptions are marked as "optional" in the specification for this
 * interface.</p>
 * 
 * @param <E> the type of elements maintained by this bag
 * 
 * @author Thiago Reis
 * @see Collection
 * @see AbstractBag
 * @see HashBag
 * @see LinkedHashBag
 * @see WeakHashBag
 * @see IdentityHashBag
 * @see EnumBag
 * @see NavigableBag
 * @see SortedBag
 * @see TreeBag
 * see Bags#singleton(java.lang.Object)
 * see Bags#EMPTY_BAG
 * @since 1.0
 */
public interface Bag<E> extends Collection<E> {
	/**
	 * Returns the number of element occurrences in this bag.
	 * If this bag contains more than <tt>Integer.MAX_VALUE</tt>
	 * elements, returns <tt>Integer.MAX_VALUE</tt>.
	 * 
	 * @return the number of element occurrences in this bag
	 */
	@Override int size();

	/**
	 * Returns <tt>true</tt> if this bag contains no element occurrences.
	 * 
	 * @return <tt>true</tt> if this bag contains no element occurrences
	 */
	@Override boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this bag contains at least one occurrence of the specified element.
	 * More formally, returns <tt>true</tt> if and only if this bag contains an element <tt>e</tt>
	 * such that <tt>(object==null ? element==null : object.equals(element))</tt>.
	 * 
	 * @param  object element whose presence in this bag is to be tested
	 * @return <tt>true</tt> if this bag contains at least one occurrence of the specified element
	 * @throws ClassCastException if the type of the specified element is incompatible with
	 *         this bag (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this bag does not permit
	 *         null elements (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	@Override boolean contains(Object object);

	/**
	 * Returns the number of occurrences of the specified element in this bag.
	 * More formally, returns a positive, non zero value if and only if this bag contains an element <tt>e</tt>
	 * such that <tt>(object==null ? element==null : object.equals(element))</tt>, zero otherwise.
	 * 
	 * @param  object element whose the number of occurrences in this bag is to be counted
	 * @return the number of occurrences of the specified element in this bag
	 *         or zero if this bag does not contain any occurreces of the specified element
	 * @throws ClassCastException if the type of the specified element is incompatible with
	 *         this bag (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this bag does not permit
	 *         null elements (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	int count(Object object);

	/**
	 * Returns an iterator over the element occurrences in this bag. The element
	 * occurrences are returned in no particular order (unless this bag is an
	 * instance of some class that provides a guarantee).
	 * 
	 * @return an iterator over the element occurrences in this bag
	 */
	@Override Iterator<E> iterator();

	/**
	 * Returns a {@link Set} view of the elements contained in this bag.
	 * The set is backed by the bag, so changes to the bag are
	 * reflected in the bag, and vice-versa.  If the bag is modified
	 * while an iteration over the set is in progress (except through
	 * the iterator's own <tt>remove</tt> operation), the results of
	 * the iteration are undefined. The set supports element removal,
	 * which deletes the corresponding element from the bag, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
	 * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
	 * operations. It does not support the <tt>add</tt> or <tt>addAll</tt>
	 * operations.
	 *
	 * @return a set view of the elements contained in this bag
	 */
	Set<E> asElementSet();

	/**
	 * Returns a {@link Set} view of the element-count pairs contained in this bag.
	 * The set is backed by the bag, so changes to the bag are
	 * reflected in the set, and vice-versa. If the bag is modified
	 * while an iteration over the set is in progress (except through
	 * the iterator's own <tt>remove</tt> operation, or through the
	 * <tt>setCount</tt> operation on a bag entry returned by the
	 * iterator) the results of the iteration are undefined. The set
	 * supports element removal, which removes the corresponding
	 * mapping from the bag, via the <tt>Iterator.remove</tt>,
	 * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
	 * <tt>clear</tt> operations. It does not support the
	 * <tt>add</tt> or <tt>addAll</tt> operations.
	 * 
	 * @return a set view of the element-count pairs contained in this bag
	 */
	Set<Entry<E>> asEntrySet();

	/**
	 * Returns an array containing all of the element occurrences in this bag.
	 * If this bag makes any guarantees as to what order its element occurrences
	 * are returned by its iterator, this method must return the elements in the
	 * same order.
	 * 
	 * <p>The returned array will be "safe" in that no references to it
	 * are maintained by this bag. (In other words, this method must
	 * allocate a new array even if this bag is backed by an array).
	 * The caller is thus free to modify the returned array.</p>
	 * 
	 * <p>This method acts as bridge between array-based and
	 * collection-based APIs.</p>
	 * 
	 * @return an array containing all the element occurrences in this bag
	 */
	@Override Object[] toArray();

	/**
	 * Returns an array containing all of the element occurrences in this bag;
	 * the runtime type of the returned array is that of the specified array.
	 * If the bag fits in the specified array, it is returned therein.
	 * Otherwise, a new array is allocated with the runtime type of the
	 * specified array and the size of this bag.
	 * 
	 * <p>If this bag fits in the specified array with room to spare
	 * (i.e., the array has more elements than this bag), the element in
	 * the array immediately following the end of the bag is set to
	 * <tt>null</tt>. (This is useful in determining the length of this
	 * bag <i>only</i> if the caller knows that this bag does not contain
	 * any null elements.)</p>
	 * 
	 * <p>If this bag makes any guarantees as to what order its elements
	 * are returned by its iterator, this method must return the elements
	 * in the same order.</p>
	 * 
	 * <p>Like the {@link #toArray()} method, this method acts as bridge between
	 * array-based and collection-based APIs. Further, this method allows
	 * precise control over the runtime type of the output array, and may,
	 * under certain circumstances, be used to save allocation costs.</p>
	 * 
	 * <p>Suppose <tt>x</tt> is a bag known to contain only strings.
	 * The following code can be used to dump the bag into a newly allocated
	 * array of <tt>String</tt>:
	 * 
	 * <pre>String[] y = x.toArray(new String[0]);</pre>
	 * 
	 * Note that <tt>toArray(new Object[0])</tt> is identical in function to
	 * <tt>toArray()</tt>.
	 * 
	 * @param  array the array into which the elements of this bag are to be
	 *         stored, if it is big enough; otherwise, a new array of the same
	 *         runtime type is allocated for this purpose.
	 * @return an array containing all the elements in this bag
	 * @throws ArrayStoreException if the runtime type of the specified array
	 *         is not a supertype of the runtime type of every element in this bag
	 * @throws NullPointerException if the specified array is null
	 */
	@Override <T> T[] toArray(T[] array);

	/**
	 * Returns a set containing all of the elements in this bag.
	 * If this bag makes any guarantees as to what order its element occurrences
	 * are returned by its iterator, this method must return the elements in the
	 * same order.
	 * 
	 * <p>The returned set will be "safe" in that no references to it
	 * are maintained by this bag. (In other words, this method must
	 * allocate a new array even if this bag is backed by an array).
	 * The caller is thus free to modify the returned array.</p>
	 * 
	 * <p>This method acts as a convenience method.</p>
	 * 
	 * @param  factory a {@code Supplier} which returns a new, empty {@code Set} of the appropriate type
	 * @return an set containing all the elements in this bag
	 */
	Set<E> toSet(Supplier<Set<E>> factory);

	/**
	 * Returns a map containing all of the element-count pairs in this bag.
	 * If this bag makes any guarantees as to what order its element occurrences
	 * are returned by its iterator, this method must return the elements in the
	 * same order.
	 * 
	 * <p>The returned set will be "safe" in that no references to it
	 * are maintained by this bag. (In other words, this method must
	 * allocate a new array even if this bag is backed by an array).
	 * The caller is thus free to modify the returned array.</p>
	 * 
	 * <p>This method acts as a convenience method.</p>
	 * 
	 * @param  factory a {@code Supplier} which returns a new, empty {@code Map} of the appropriate type
	 * @return an map containing all the element-count pairs in this bag
	 */
	Map<E, Integer> toMap(Supplier<Map<E, Integer>> factory);

	/**
	 * Adds one occurrence of the specified
	 * element to this bag (optional operation).
	 *
	 * <p>The stipulation above does not imply that bags must accept all
	 * elements; bags may refuse to add any particular element, including
	 * <tt>null</tt>, and throw an exception, as described in the
	 * specification for {@link Collection#add Collection.add}. Individual
	 * bag implementations should clearly document any restrictions on the
	 * elements that they may contain.</p>
	 *
	 * @param  element element with which the occurrence
	 *         is to be added to this bag
	 * @return always <tt>true</tt>, bags are always
	 *         changed as a result of the call
	 * @throws UnsupportedOperationException if the <tt>add</tt>
	 *         operation is not supported by this bag
	 * @throws ClassCastException if the class of the specified
	 *         element prevents it from being added to this bag
	 * @throws NullPointerException if the specified element is
	 *         null and this bag does not permit null elements
	 * @throws IllegalArgumentException if some property of the
	 *         specified element prevents it from being added to this bag
	 */
	@Override boolean add(E element);

	/**
	 * Adds to or removes from this bag the specified amount of
	 * occurrences for the specified element (optional operation).
	 * 
	 * <p>The stipulation above does not imply that bags must accept all
	 * elements; bags may refuse to add any particular element, including
	 * <tt>null</tt>, and throw an exception, as described in the
	 * specification for {@link Collection#add Collection.add}. Individual
	 * bag implementations should clearly document any restrictions on the
	 * elements that they may contain.</p>
	 * 
	 * @param  element element with which the specified number of
	 *         occurrences is to be added to or removed from this bag
	 * @param  amount the number of occurrences to be added to (if positive)
	 *         or removed from (if negative) this bag for the specified element
	 * @return the previous element <tt>count</tt>, or
	 *         <tt>zero</tt> if there was no occurrence for element.
	 * @throws UnsupportedOperationException if the <tt>put</tt>
	 *         operation is not supported by this bag
	 * @throws ClassCastException if the class of the specified
	 *         element prevents it from being put to this bag
	 * @throws NullPointerException if the specified element is
	 *         null and this bag does not permit null elements
	 * @throws IllegalArgumentException if some property of the
	 *         specified element prevents it from being put to this bag
	 */
	int put(E element, int amount);

	/**
	 * Changes the number of occurrences to the specified
	 * count for the specified element (optional operation).
	 * 
	 * <p>The stipulation above does not imply that bags must accept all
	 * elements; bags may refuse to add any particular element, including
	 * <tt>null</tt>, and throw an exception, as described in the
	 * specification for {@link Collection#add Collection.add}. Individual
	 * bag implementations should clearly document any restrictions on the
	 * elements that they may contain.</p>
	 * 
	 * @param  element element with which the specified number
	 *         of occurrences is to be changed in this bag
	 * @param  count the number of occurrences to be set
	 *         for the specified element in this bag
	 * @return the previous element <tt>count</tt>, or
	 *         <tt>zero</tt> if there was no occurrence for element.
	 * @throws UnsupportedOperationException if the <tt>set</tt>
	 *         operation is not supported by this bag
	 * @throws ClassCastException if the class of the specified
	 *         element prevents it from being set to this bag
	 * @throws NullPointerException if the specified element is
	 *         null and this bag does not permit null elements
	 * @throws IllegalArgumentException if some property of the
	 *         specified element prevents it from being set to this bag
	 */
	int set(E element, int count);

	/**
	 * Removes one occurrence of the specified
	 * element from this bag (optional operation).
	 * 
	 * @param  object object with which the occurrence
	 *         is to be removed from this bag
	 * @return <tt>true</tt> if this bag had one or more occurrences
	 *         of the specified element, <tt>false</tt> otherwise
	 * @throws ClassCastException if the type of the
	 *         specified element is incompatible with this bag
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is
	 *         null and this bag does not permit null elements
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws UnsupportedOperationException if the <tt>remove</tt>
	 *         operation is not supported by this bag
	 */
	@Override boolean remove(Object object);

	/**
	 * Removes all occurrences of the specified
	 * element from this bag (optional operation).
	 * 
	 * @param  object object with which the occurrences
	 *         are to be removed from this bag
	 * @return the previous element <tt>count</tt>, or
	 *         <tt>zero</tt> if there was no occurrence for element.
	 * @throws ClassCastException if the type of the
	 *         specified element is incompatible with this bag
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is
	 *         null and this bag does not permit null elements
	 *         (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws UnsupportedOperationException if the <tt>delete</tt>
	 *         operation is not supported by this bag
	 */
	int delete(Object object);

	/**
	 * Returns <tt>true</tt> if this bag contains all of
	 * the element occurrences of the specified collection.
	 * 
	 * @param  collection collection to be checked for containment in this bag
	 * @return <tt>true</tt> if this bag contains all of the element occurrences of the specified collection
	 * @throws ClassCastException if the types of one or more elements in the specified collection are
	 *         incompatible with this bag (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified collection contains one or more null elements and this bag
	 *         does not permit null elements (<a href="Collection.html#optional-restrictions">optional</a>),
	 *         or if the specified collection is null
	 * @see    #contains(Object)
	 */
	@Override boolean containsAll(Collection<?> collection);

	/**
	 * Adds all of the element occurrences in the specified
	 * collection to this bag (optional operation).
	 * 
	 * The behavior of this operation is undefined if the specified
	 * collection is modified while the operation is in progress.
	 * 
	 * @param  collection collection containing element occurrences to be added to this bag
	 * @return <tt>true</tt> if the specified collection is not <tt>empty</tt>
	 *         (bags are always added as a result of the call), <tt>false</tt> otherwise
	 * @throws UnsupportedOperationException if the <tt>addAll</tt> operation is not supported by this bag
	 * @throws ClassCastException if the class of an element of the specified
	 *         collection prevents it from being added to this bag
	 * @throws NullPointerException if the specified collection contains one
	 *         or more null elements and this bag does not permit null
	 *         elements, or if the specified collection is null
	 * @throws IllegalArgumentException if some property of an element of the
	 *         specified collection prevents it from being added to this bag
	 * @see #add(Object)
	 */
	@Override boolean addAll(Collection<? extends E> collection);

	/**
	 * Removes from this bag all of its element occurrences that are
	 * contained in the specified collection (optional operation).
	 * If the specified collection is also a bag, this operation
	 * effectively modifies this bag so that its value is the
	 * <i>asymmetric set difference</i> of the two bags
	 * (considering multiple occurrences of their elements).
	 * 
	 * @param  collection collection containing element occurrences to be removed from this bag
	 * @return <tt>true</tt> if this bag had one or more element occurrence of any
	 *         of the specified collection elements, <tt>false</tt> otherwise
	 * @throws UnsupportedOperationException if the <tt>removeAll</tt> operation
	 *         is not supported by this bag
	 * @throws ClassCastException if the class of an element of this bag is incompatible with the
	 *         specified collection (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if this bag contains a null element and the specified collection
	 *         does not permit null elements (<a href="Collection.html#optional-restrictions">optional</a>),
	 *         or if the specified collection is null
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override boolean removeAll(Collection<?> collection);

	/**
	 * Retains only the element occurrences in this bag that are contained in
	 * the specified collection (optional operation). In other words, removes
	 * from this bag all of its element occurrences that do not exist in the
	 * specified collection. If the specified collection is also a bag, this
	 * operation effectively modifies this bag so that its value is the
	 * <i>intersection</i> of the two bags (considering multiple occurrences
	 * of their elements).
	 *
	 * @param  collection collection containing element occurrences to be retained in this bag
	 * @return <tt>true</tt> if this bag had one or more element occurrences that do
	 *         not exist in the specified collection, <tt>false</tt> otherwise
	 * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
	 *         is not supported by this bag
	 * @throws ClassCastException if the class of an element of this bag is incompatible with the
	 *         specified collection (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if this bag contains a null element and the specified collection
	 *         does not permit null elements (<a href="Collection.html#optional-restrictions">optional</a>),
	 *         or if the specified collection is null
	 * @see #remove(Object)
	 */
	@Override boolean retainAll(Collection<?> collection);

	/**
	 * Adds one occurrence to this bag for each element that satisfies the given
	 * predicate (optional operation). Errors or runtime exceptions thrown during
	 * iteration or by the predicate are relayed to the caller.
	 * 
	 * @param  filter a predicate which returns {@code true} for the
	 *         element with which one occurrence is to be added to this bag
	 * @return {@code true} if any occurrence was added to this bag
	 * @throws NullPointerException if the specified filter is null
	 * @throws UnsupportedOperationException if occurrences cannot be
	 *         added to this bag. Implementations may throw this exception
	 *         if a matching element cannot have an occurrence added or if,
	 *         in general, addition is not supported.
	 * @since 1.0
	 */
	boolean addIf(Predicate<? super E> filter);

	/**
	 * Adds to or removes from this bag the specified amount of
	 * occurrences for each element that satisfies the given predicate
	 * (optional operation). Errors or runtime exceptions thrown during
	 * iteration or by the predicate are relayed to the caller.
	 * 
	 * @param  filter a predicate which returns {@code true} for the
	 *         element with which occurrences is to be added to or
	 *         removed from this bag
	 * @param  amount the number of occurrences to be added to (if positive)
	 *         or removed from (if negative) this bag for each element that
	 *         satisfies the given predicate
	 * @return {@code true} if any occurrence was added to or removed
	 *         from this bag
	 * @throws NullPointerException if the specified filter is null
	 * @throws UnsupportedOperationException if occurrences cannot be
	 *         added to this bag. Implementations may throw this exception
	 *         if a matching element cannot have an occurrence added or if,
	 *         in general, addition is not supported.
	 * @since 1.0
	 */
	boolean putIf(Predicate<? super E> filter, int amount);

	/**
	 * Changes the number of occurrences to the specified count for each
	 * element that satisfies the given predicate (optional operation).
	 * Errors or runtime exceptions thrown during iteration or by the
	 * predicate are relayed to the caller.
	 * 
	 * @param  filter a predicate which returns {@code true} for the
	 *         element with which occurrences is to be changed in this bag
	 * @param  count the number of occurrences to be set in this bag
	 *         for each element that satisfies the given predicate
	 * @return {@code true} if any occurrence was added to or removed
	 *         from this bag
	 * @throws NullPointerException if the specified filter is null
	 * @throws UnsupportedOperationException if occurrences cannot be
	 *         added to or removed from this bag. Implementations may
	 *         throw this exception if a matching element cannot have
	 *         an occurrence added or removed or if, in general, addition
	 *         or removal is not supported.
	 * @since 1.0
	 */
	boolean setIf(Predicate<? super E> filter, int count);

	/**
	 * Removes all of the element occurrences of this bag that satisfy the given
	 * predicate. Errors or runtime exceptions thrown during iteration or by
	 * the predicate are relayed to the caller.
	 * 
	 * @param  filter a predicate which returns {@code true} for element occurrence to be removed
	 * @return {@code true} if any element occurrences were removed
	 * @throws NullPointerException if the specified filter is null
	 * @throws UnsupportedOperationException if element occurrences cannot be removed
	 *         from this bag. Implementations may throw this exception if a matching element
	 *         occurrence cannot be removed or if, in general, removal is not supported.
	 * @since 1.0
	 */
	@Override boolean removeIf(Predicate<? super E> filter);

	/**
	 * Removes all of the element occurrences from this
	 * bag (optional operation). The bag will be empty
	 * after this call returns.
	 *
	 * @throws UnsupportedOperationException if the <tt>clear</tt>
	 *         method is not supported by this bag
	 */
	@Override void clear();

	/**
	 * Returns the hash code value for this bag. The hash code of a bag is
	 * defined to be the sum of the hash codes of each entry in the bag's
	 * <tt>asEntrySet()</tt> view. This ensures that <tt>b1.equals(b2)</tt>
	 * implies that <tt>b1.hashCode()==b2.hashCode()</tt> for any two bags
	 * <tt>b1</tt> and <tt>b2</tt>, as required by the general contract of
	 * {@link Object#hashCode}.
	 * 
	 * @return the hash code value for this bag
	 * @see Bag.Entry#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	@Override int hashCode();

	/**
	 * Compares the specified object with this bag for equality. Returns
	 * <tt>true</tt> if the specified object is also a bag, the two bags
	 * have the same size, and every member occurrence of the specified bag
	 * is contained in this bag (or equivalently, every member occurrence
	 * of this bag is contained in the specified bag). This definition ensures
	 * that the equals method works properly across different implementations
	 * of the bag interface.
	 * 
	 * @param  object object to be compared for equality with this bag
	 * @return <tt>true</tt> if the specified object is equal to this bag
	 */
	@Override boolean equals(Object object);

	/**
	 * Returns a string representation of this bag. The string representation
	 * consists of a list of element-count pairs in the order returned by the
	 * bag's <tt>asEntrySet</tt> view's iterator, enclosed in square braces
	 * (<tt>"[]"</tt>). Adjacent pairs are separated by the characters
	 * <tt>", "</tt> (comma and space). Each element-count pair is rendered as
	 * the element followed by an equals sign (<tt>"="</tt>) followed by the
	 * associated count. Elements are converted to strings as by
	 * {@link String#valueOf(Object)}.
	 * 
	 * @return a string representation of this bag
	 */
	@Override String toString();

	/**
	 * A bag entry (element-count pair). The <tt>Bag.asEntrySet</tt> method
	 * returns a set-view of the bag, whose elements are of this class. The
	 * <i>only</i> way to obtain a reference to a bag entry is from the
	 * iterator of this set-view. These <tt>Bag.Entry</tt> objects are
	 * valid <i>only</i> for the duration of the iteration; more formally,
	 * the behavior of a bag entry is undefined if the backing bag has been
	 * modified after the entry was returned by the iterator, except through
	 * the <tt>setCount</tt> operation on the bag entry.
	 * 
	 * @param <E> the type of element maintained by this entry
	 * 
	 * @see Bag#asEntrySet()
	 * @since 1.0
	 */
	interface Entry<E> {
		/**
		 * Returns the element corresponding to this entry.
		 * 
		 * @return the element corresponding to this entry
		 * @throws IllegalStateException implementations may, but are not
		 *         required to, throw this exception if the entry has been
		 *         removed from the backing bag.
		 */
		E getElement();

		/**
		 * Returns the element count corresponding to this entry. If the
		 * entry has been removed from the backing bag (by the iterator's
		 * <tt>remove</tt> operation), the results of this call are undefined.
		 * 
		 * @return the element count corresponding to this entry
		 * @throws IllegalStateException implementations may, but are not
		 *         required to, throw this exception if the entry has been
		 *         removed from the backing bag.
		 */
		int getCount();

		/**
		 * Replaces the element count corresponding to this entry with the
		 * specified count (optional operation). (Writes through to the bag.)
		 * The behavior of this call is undefined if the entry has already been
		 * removed from the bag (by the iterator's <tt>remove</tt> operation).
		 * 
		 * @param  count new count to be stored in this entry
		 * @return old count corresponding to the entry
		 * @throws UnsupportedOperationException if modifications
		 *         operations are not supported by the backing bag
		 * @throws NullPointerException if the the specified count is null
		 * @throws IllegalArgumentException if count is negative
		 * @throws IllegalStateException implementations may, but are not required to,
		 *         throw this exception if the entry has been removed from the backing bag.
		 */
		int setCount(int count);

		/**
		 * Compares the specified object with this entry for equality.
		 * Returns <tt>true</tt> if the given object is also a bag entry and
		 * the two entries represent the same element and count. More formally,
		 * two entries <tt>e1</tt> and <tt>e2</tt> represent the same element and count
		 * if
		 * <pre>
		 *     (e1.getElement()==null ?
		 *      e2.getElement()==null : e1.getElement().equals(e2.getElement())) &amp;&amp;
		 *     (e1.getCount()==e2.getCount())
		 * </pre>
		 * This ensures that the <tt>equals</tt> method works properly across
		 * different implementations of the <tt>Bag.Entry</tt> interface.
		 *
		 * @param  object object to be compared for equality with this bag entry
		 * @return <tt>true</tt> if the specified object is equal to this bag entry
		 */
		@Override boolean equals(Object object);

		/**
		 * Returns the hash code value for this bag entry.
		 * The hash code of a bag entry <tt>e</tt> is defined to be:
		 * <pre>
		 *     (e.getElement()==null ? 0 : e.getElement().hashCode()) ^ e.getCount()
		 * </pre>
		 * This ensures that <tt>e1.equals(e2)</tt> implies that
		 * <tt>e1.hashCode()==e2.hashCode()</tt> for any two Entries
		 * <tt>e1</tt> and <tt>e2</tt>, as required by the general
		 * contract of <tt>Object.hashCode</tt>.
		 *
		 * @return the hash code value for this map entry
		 * @see Object#hashCode()
		 * @see Object#equals(Object)
		 * @see #equals(Object)
		 */
		@Override int hashCode();

		/**
		 * Returns a comparator that compares {@link Bag.Entry}
		 * in elements natural order.
		 * 
		 * <p>The returned comparator is serializable and throws
		 * {@link NullPointerException} when comparing an entry
		 * with a null element.</p>
		 * 
		 * @param  <E> the {@link Comparable} type of the bag elements
		 * @return a comparator that compares {@link Bag.Entry} in elements natural order.
		 * 
		 * @see Comparable
		 * @since 1.0
		 */
		public static <E extends Comparable<? super E>> Comparator<Entry<E>> comparingByElement() {
			return (Comparator<Entry<E>> & Serializable)(entry1, entry2) -> entry1.getElement().compareTo(entry2.getElement());
		}

		/**
		 * Returns a comparator that compares {@link Bag.Entry}
		 * in natural order on elements counts.
		 * 
		 * <p>The returned comparator is serializable.</p>
		 * 
		 * @param  <E> the type of the bag elements
		 * @return a comparator that compares {@link Bag.Entry}
		 *         in natural order on elements counts.
		 * 
		 * @see Comparable
		 * @since 1.0
		 */
		public static <E> Comparator<Entry<E>> comparingByCount() {
			return (Comparator<Entry<E>> & Serializable)(entry1, entry2) -> Integer.compare(entry1.getCount(), entry2.getCount());
		}

		/**
		 * Returns a comparator that compares {@link Bag.Entry}
		 * by element using the given {@link Comparator}.
		 * 
		 * <p>The returned comparator is serializable if
		 * the specified comparator is also serializable.</p>
		 * 
		 * @param  <E> the type of the bag elements
		 * @param  comparator the elements {@link Comparator}
		 * @return a comparator that compares {@link Bag.Entry} by the element
		 * 
		 * @since 1.0
		 */
		public static <E> Comparator<Entry<E>> comparingByElement(Comparator<? super E> comparator) {
			return (Comparator<Entry<E>> & Serializable)(entry1, entry2) -> Objects.requireNonNull(comparator, "Invalid null comparator.").compare(entry1.getElement(), entry2.getElement());
		}

		/**
		 * Returns a comparator that compares {@link Bag.Entry}
		 * by elements counts using the given {@link Comparator}.
		 * 
		 * <p>The returned comparator is serializable if
		 * the specified comparator is also serializable.</p>
		 * 
		 * @param  <E> the type of the bag elements
		 * @param  comparator the elements counts {@link Comparator}
		 * @return a comparator that compares {@link Bag.Entry} by the elements counts
		 * 
		 * @since 1.0
		 */
		public static <E> Comparator<Entry<E>> comparingByCount(Comparator<? super Integer> comparator) {
			return (Comparator<Entry<E>> & Serializable)(entry1, entry2) -> Objects.requireNonNull(comparator, "Invalid null comparator.").compare(entry1.getCount(), entry2.getCount());
		}
	}
}
