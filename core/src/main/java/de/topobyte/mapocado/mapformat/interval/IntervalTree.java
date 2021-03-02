package de.topobyte.mapocado.mapformat.interval;

import java.util.List;

public interface IntervalTree<S extends Comparable<S>, T>
{

	/**
	 * Get the object associated with the interval the given value lies within.
	 * 
	 * @param value
	 *            the value to request an object for.
	 * @return the object associated with the interval that values lies within.
	 */
	public T getObject(S value);

	/**
	 * Get all object associated with the intervals <= the interval the given
	 * value lies within.
	 * 
	 * @param value
	 *            the value to request objects for.
	 * @return the objects associated with the intervals <=the interval the
	 *         given value lies within.
	 */
	public List<T> getObjects(S value);

	/**
	 * Get all objects in the tree.
	 * 
	 * @return a list of all objects in the tree.
	 */
	public List<T> getAllObjects();

	/**
	 * Get the list of the starting elements of the intervals.
	 * 
	 * @return the list of interval starts.
	 */
	public List<S> getIntervalStarts();
}
