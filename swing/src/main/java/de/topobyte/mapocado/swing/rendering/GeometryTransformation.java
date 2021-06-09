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

package de.topobyte.mapocado.swing.rendering;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.CoordinateTransformer;
import de.topobyte.mapocado.mapformat.geom.LinearRing;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.geom.Polygon;
import de.topobyte.mapocado.rendering.Clipping;

public class GeometryTransformation
{
	public static Path2D getPath(Linestring string, CoordinateTransformer ct)
	{
		Path2D.Double path = new Path2D.Double();
		path.moveTo(ct.getX(string.x[0]), ct.getY(string.y[0]));
		for (int i = 1; i < string.getNumberOfCoordinates(); i++) {
			path.lineTo(ct.getX(string.x[i]), ct.getY(string.y[i]));
		}
		return path;
	}

	/**
	 * Convert a Linestring to a list of Paths such that each path in the
	 * returned list has a maximal size of 'length'
	 * 
	 * TODO: this method is not used anymore, but hey, it has been difficult to
	 * implement and by be usable someday.
	 */
	public static List<Path2D> getChunkedPath(Linestring string,
			CoordinateTransformer ct, double length)
	{
		List<Path2D> paths = new ArrayList<>();

		Path2D.Double path = new Path2D.Double();
		paths.add(path);
		double pathLen = 0;

		float lx = ct.getX(string.x[0]);
		float ly = ct.getY(string.y[0]);
		path.moveTo(lx, ly);
		for (int i = 1; i < string.getNumberOfCoordinates(); i++) {
			float cx = ct.getX(string.x[i]);
			float cy = ct.getY(string.y[i]);
			float dx = cx - lx;
			float dy = cy - ly;
			double segLen = Math.sqrt(dx * dx + dy * dy);

			double left = segLen;
			double tx = lx;
			double ty = ly;
			while (left > 0) {
				if (pathLen + left <= length) {
					// complete segment goes into this chunk
					pathLen += left;
					path.lineTo(cx, cy);
					break;
				} else {
					// only a part is needed
					double needed = length - pathLen;
					double useFraction = needed / segLen;
					double sx = tx + dx * useFraction;
					double sy = ty + dy * useFraction;
					path.lineTo(sx, sy);
					tx = sx;
					ty = sy;
					left -= needed;

					// begin a new path
					path = new Path2D.Double();
					pathLen = 0;
					paths.add(path);
					path.moveTo(sx, sy);
				}
			}
			lx = cx;
			ly = cy;
		}

		return paths;
	}

	public static Path2D getClippedPath(Linestring string,
			CoordinateTransformer ct, Clipping clipping)
	{
		Path2D.Double path = new Path2D.Double();
		int numPoints = string.getNumberOfCoordinates();

		boolean started = false;
		for (int i = 0; i < numPoints - 1; i++) {
			float x0 = ct.getX(string.x[i]);
			float y0 = ct.getY(string.y[i]);
			float x1 = ct.getX(string.x[i + 1]);
			float y1 = ct.getY(string.y[i + 1]);
			boolean relevant = clipping.isNotOutside(x0, y0, x1, y1);

			if (relevant) {
				if (!started) {
					path.moveTo(x0, y0);
					started = true;
				}
				path.lineTo(x1, y1);
			} else {
				started = false;
			}
		}

		return path;
	}

	public static Area getArea(LinearRing ring, CoordinateTransformer ct)
	{
		Path2D.Double path = new Path2D.Double();
		path.moveTo(ct.getX(ring.x[0]), ct.getY(ring.y[0]));
		for (int i = 1; i < ring.getNumberOfCoordinates(); i++) {
			path.lineTo(ct.getX(ring.x[i]), ct.getY(ring.y[i]));
		}
		path.closePath();
		return new Area(path);
	}

	public static Area toShape(Polygon p, CoordinateTransformer ct)
	{
		if (p.isEmpty()) {
			return new Area();
		}

		LinearRing ring = p.getExteriorRing();
		Area outer = getArea(ring, ct);

		for (int i = 0; i < p.getNumberOfInteriorRings(); i++) {
			LinearRing interior = p.getInteriorRing(i);
			Area inner = getArea(interior, ct);
			outer.subtract(inner);
		}

		return outer;
	}

	public static Area toShape(Multipolygon mp, CoordinateTransformer ct)
	{
		Area area = new Area();
		for (int i = 0; i < mp.getNumberOfPolygons(); i++) {
			Polygon polygon = mp.getPolygon(i);
			Area a = toShape(polygon, ct);
			area.add(a);
		}
		return area;
	}
}
