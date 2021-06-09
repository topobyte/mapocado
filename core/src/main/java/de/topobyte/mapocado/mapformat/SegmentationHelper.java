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

package de.topobyte.mapocado.mapformat;

import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.util.ObjectClassLookup;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;

public class SegmentationHelper
{

	public static int getMinimumZoomLevel(Entity element,
			ObjectClassLookup classLookup)
	{
		int minZoom = Integer.MAX_VALUE;
		for (int refId : element.getClasses().toArray()) {
			ObjectClassRef ref = classLookup.get(refId);
			int min = ref.getMinZoom();
			if (min < minZoom) {
				minZoom = min;
			}
		}
		return minZoom;
	}

}
