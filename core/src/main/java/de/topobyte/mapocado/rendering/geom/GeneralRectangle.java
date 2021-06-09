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

package de.topobyte.mapocado.rendering.geom;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import com.infomatiq.jsi.Rectangle;

/**
 * Rectangles in general orientation.
 * 
 * A rectangle is stored in an int-array of size 8. The array contains the
 * coordinates in the following order: [x1, y1, x2, y2, x3, y3, x4, y4]
 * 
 * This class provides utility methods for manipulating such rectangles.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class GeneralRectangle
{

	public static Rectangle getBoundingBox(int[] box)
	{
		int x = box[0];
		int y = box[1];
		int minX = x, maxX = x, minY = y, maxY = y;
		for (int i = 2; i < 8; i += 2) {
			x = box[i];
			y = box[i + 1];
			if (x < minX)
				minX = x;
			if (x > maxX)
				maxX = x;
			if (y < minY)
				minY = y;
			if (y > maxY)
				maxY = y;
		}
		return new Rectangle(minX, minY, maxX, maxY);
	}

	public static boolean intersects(int[] box1, int[] box2)
	{
		Polygon polygon1 = createPolygon(box1);
		Polygon polygon2 = createPolygon(box2);
		return polygon1.intersects(polygon2);
	}

	private static Polygon createPolygon(int[] box)
	{
		GeometryFactory factory = new GeometryFactory();
		Coordinate[] coordinates = new Coordinate[4];
		for (int i = 0; i < 4; i++) {
			coordinates[i] = new Coordinate(box[i * 2], box[i * 2 + 1]);
		}
		LinearRing shell = factory.createLinearRing(coordinates);
		return factory.createPolygon(shell, null);
	}

}
