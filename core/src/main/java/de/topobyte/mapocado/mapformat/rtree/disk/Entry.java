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

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;

/**
 * An entry is a structure used for describing entries of nodes. They have a
 * bounding box denoted by values x1, x2, y1 and y2. Depending on the type of
 * entry it may also contain information about address or size of the described
 * element.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class Entry
{
	public int x1, x2, y1, y2;
	int address;
	int size;

	public Entry(BoundingBox box, int address)
	{
		this(box.getMinX(), box.getMaxX(), box.getMinY(), box.getMaxY(),
				address);
	}

	public Entry(int x1, int x2, int y1, int y2, int address)
	{
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.address = address;
	}

	public boolean intersects(BoundingBox box)
	{
		return x2 >= box.getMinX() && x1 <= box.getMaxX() && y2 >= box.getMinY()
				&& y1 <= box.getMaxY();
	}

	public int getX1()
	{
		return x1;
	}

	public int getX2()
	{
		return x2;
	}

	public int getY1()
	{
		return y1;
	}

	public int getY2()
	{
		return y2;
	}

	public BoundingBox getBoundingBox()
	{
		return new BoundingBox(x1, x2, y1, y2);
	}

	@Override
	public String toString()
	{
		return getBoundingBox().toString();
	}
}