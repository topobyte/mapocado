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

package de.topobyte.mapocado.mapformat.io;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class StartPointCalculator
{

	private int n = 0;
	private double x = 0;
	private double y = 0;

	public void add(Point point)
	{
		n++;
		x += point.getX();
		y += point.getY();
		// System.out.println(point);
		// System.out.println(point.getX() + "; " + point.getY());
		// System.out.println(String.format("%d: %f, %f", n, x, y));
		if (Double.isNaN(x)) {
			System.exit(1);
		}
	}

	public Point getPoint()
	{
		Coordinate coordinate = new Coordinate(x / n, y / n);
		return new GeometryFactory().createPoint(coordinate);
	}

}
