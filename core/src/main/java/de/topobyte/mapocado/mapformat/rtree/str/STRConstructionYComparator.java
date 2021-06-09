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

import java.util.Comparator;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;

public class STRConstructionYComparator<T>
		implements Comparator<STRConstructionElement<T>>
{

	@Override
	public int compare(STRConstructionElement<T> o1,
			STRConstructionElement<T> o2)
	{
		BoundingBox box1 = o1.getBoundingBox();
		BoundingBox box2 = o2.getBoundingBox();

		float y1 = box1.getCenterY();
		float y2 = box2.getCenterY();
		if (y1 < y2) {
			return -1;
		}
		if (y1 > y2) {
			return 1;
		}

		float x1 = box1.getCenterX();
		float x2 = box2.getCenterX();
		if (x1 < x2) {
			return -1;
		}
		if (x1 > x2) {
			return 1;
		}

		return 0;
	}

}
