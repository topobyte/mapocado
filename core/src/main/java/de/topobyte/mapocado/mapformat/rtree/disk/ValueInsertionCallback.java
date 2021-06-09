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

package de.topobyte.mapocado.mapformat.rtree.disk;

public interface ValueInsertionCallback<T>
{

	/**
	 * Notify a listener about the insertion of an element into the tree.
	 * 
	 * @param entryAddress
	 *            the address of the tree leaf that contains the value
	 * @param childNumber
	 *            the index of the value within the array of values the leaf
	 *            stores
	 * @param thing
	 *            the actual thing that has been inserted
	 */
	public void valueInserted(int entryAddress, int childNumber, T thing);

}
