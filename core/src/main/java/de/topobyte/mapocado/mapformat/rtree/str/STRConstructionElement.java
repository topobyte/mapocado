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

package de.topobyte.mapocado.mapformat.rtree.str;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;

public class STRConstructionElement<T>
{
	private final BoundingBox rect;
	private final T object;

	public STRConstructionElement(BoundingBox rect, T object)
	{
		this.rect = rect;
		this.object = object;
	}

	public BoundingBox getBoundingBox()
	{
		return rect;
	}

	public T getObject()
	{
		return object;
	}

}
