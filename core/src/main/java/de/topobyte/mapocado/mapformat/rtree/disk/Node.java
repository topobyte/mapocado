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

import java.io.IOException;
import java.util.List;

/**
 * A node may be an inner node or a leaf. An inner node contains just nodes or
 * just leafs. Leafs contains just elements. The isLeaf property distinguishes
 * nodes from leafs.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class Node
{

	final boolean isLeaf;
	final List<Entry> entries;

	public Node(boolean isLeaf, List<Entry> entries) throws IOException
	{
		this.isLeaf = isLeaf;
		this.entries = entries;
	}

	public List<Entry> getEntries()
	{
		return entries;
	}

}