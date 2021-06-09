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

package de.topobyte.mapocado.mapformat.rtree.disk.cache;

import de.topobyte.mapocado.mapformat.rtree.disk.Node;

public interface InnerNodeCache
{
	/**
	 * Retrieve the node at the specified address from the cache if available.
	 * 
	 * @param address
	 *            the address this node is at in the original file.
	 * @param depth
	 *            the depth of the node within the tree.
	 * @return the node from cache or null if not available.
	 */
	public Node get(int address, int depth);

	/**
	 * Tell the cache that the specified element has been retrieved from the
	 * underlying file. This method allows the cache to insert elements into its
	 * internal structure.
	 * 
	 * @param address
	 *            the address this node is at in the original file.
	 * @param node
	 *            the node to store.
	 * @param depth
	 *            the depth of the node within the tree.
	 * 
	 */
	public void put(int address, int depth, Node node);
}
