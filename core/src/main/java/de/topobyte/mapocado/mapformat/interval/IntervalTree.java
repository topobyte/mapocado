// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

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
