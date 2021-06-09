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

import java.util.ArrayList;
import java.util.List;

public class NaiveIntervalTree<S extends Comparable<S>, T>
		implements IntervalTree<S, T>
{

	private List<S> intervals = new ArrayList<>();
	private List<T> objects = new ArrayList<>();

	/**
	 * Create a new IntervalTree with the intervals starting with the values
	 * given in the intervalStarts list. For example if you create an
	 * IntervalTree with a list containing the items [2, 5, 12] you will you
	 * will get a tree with the intervals [-infinity, 2[, [2, 5[, [5, 12[, [12,
	 * +infinity]. The length of values must be length of intervalStarts + 1.
	 * 
	 * @param intervalStarts
	 *            the list defining the intervals managed by the structure.
	 * 
	 * @param values
	 *            the values attached to the intervals.
	 */
	public NaiveIntervalTree(List<S> intervalStarts, List<T> values)
	{
		// check validity of the lengths of the supplied lists.
		if (values.size() != intervalStarts.size() + 1) {
			throw new IllegalArgumentException(
					"length of values list has to be equal to length of interval list + 1");
		}
		// check strict monotonicity of intervals values.
		if (intervalStarts.size() > 0) {
			S last = intervalStarts.get(0);
			for (int i = 1; i < intervalStarts.size(); i++) {
				S current = intervalStarts.get(i);
				if (!(last.compareTo(current) < 0)) {
					throw new IllegalArgumentException(
							"interval values need to be strictly increasing.");
				}
				last = current;
			}
		}
		// sane values supplied. initialize.
		intervals.addAll(intervalStarts);
		objects.addAll(values);
	}

	@Override
	public T getObject(S value)
	{
		int i = 0;
		for (i = 0; i < intervals.size(); i++) {
			S bound = intervals.get(i);
			if (value.compareTo(bound) < 0) {
				break;
			}
		}
		return objects.get(i);
	}

	@Override
	public List<T> getObjects(S value)
	{
		List<T> results = new ArrayList<>();
		int i = 0;
		for (i = 0; i < intervals.size(); i++) {
			S bound = intervals.get(i);
			results.add(objects.get(i));
			if (value.compareTo(bound) < 0) {
				break;
			}
			if (i == intervals.size() - 1) {
				results.add(objects.get(i + 1));
			}
		}
		return results;
	}

	@Override
	public List<S> getIntervalStarts()
	{
		return intervals;
	}

	@Override
	public List<T> getAllObjects()
	{
		return objects;
	}

}
