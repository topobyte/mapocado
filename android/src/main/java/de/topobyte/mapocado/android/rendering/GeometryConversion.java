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

package de.topobyte.mapocado.android.rendering;

import android.graphics.Path;
import de.topobyte.mapocado.mapformat.CoordinateTransformer;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.mapformat.geom.LinearRing;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.geom.Polygon;
import de.topobyte.mapocado.rendering.Clipping;

/**
 * Utility methods for converting geometries from mercator representations to
 * on-tile, android-friendly items that may be converted to drawable objects
 * very fast.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 */
public class GeometryConversion
{

	public static Path createPath(Linestring string, boolean reverse,
			CoordinateTransformer mercator)
	{
		Path path = new Path();
		createPath(path, string, reverse, mercator);
		return path;
	}

	public static void createPath(Path path, Linestring string, boolean reverse,
			CoordinateTransformer mercator)
	{
		int numPoints = string.getNumberOfCoordinates();

		if (!reverse) {
			float x = (float) mercator.getX(string.x[0]);
			float y = (float) mercator.getY(string.y[0]);
			path.moveTo(x, y);

			for (int i = 1; i < numPoints; i++) {
				x = (float) mercator.getX(string.x[i]);
				y = (float) mercator.getY(string.y[i]);
				path.lineTo(x, y);
			}
		} else {
			float x = mercator.getX(string.x[numPoints - 1]);
			float y = mercator.getY(string.y[numPoints - 1]);
			path.moveTo(x, y);

			for (int i = numPoints - 2; i >= 0; i--) {
				x = mercator.getX(string.x[i]);
				y = mercator.getY(string.y[i]);
				path.lineTo(x, y);
			}
		}
	}

	public static void createClippedPath(Path path, Linestring string,
			Mercator mercator, Clipping clipping)
	{
		int numPoints = string.getNumberOfCoordinates();

		boolean started = false;
		for (int i = 0; i < numPoints - 1; i++) {
			float x0 = mercator.getX(string.x[i]);
			float y0 = mercator.getY(string.y[i]);
			float x1 = mercator.getX(string.x[i + 1]);
			float y1 = mercator.getY(string.y[i + 1]);
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
	}

	public static double createPathAndMeasure(Path path, Linestring string,
			boolean reverse, CoordinateTransformer mercator)
	{
		double length = 0;
		int numPoints = string.getNumberOfCoordinates();

		if (!reverse) {
			float lastX = mercator.getX(string.x[0]);
			float lastY = mercator.getY(string.y[0]);
			path.moveTo(lastX, lastY);

			for (int i = 1; i < numPoints; i++) {
				float x = mercator.getX(string.x[i]);
				float y = mercator.getY(string.y[i]);
				float dx = x - lastX;
				float dy = y - lastY;
				length += Math.sqrt(dx * dx + dy * dy);
				path.lineTo(x, y);
				lastX = x;
				lastY = y;
			}
		} else {
			float lastX = mercator.getX(string.x[numPoints - 1]);
			float lastY = mercator.getY(string.y[numPoints - 1]);
			path.moveTo(lastX, lastY);

			for (int i = numPoints - 2; i >= 0; i--) {
				float x = mercator.getX(string.x[i]);
				float y = mercator.getY(string.y[i]);
				float dx = x - lastX;
				float dy = y - lastY;
				length += Math.sqrt(dx * dx + dy * dy);
				path.lineTo(x, y);
				lastX = x;
				lastY = y;
			}
		}

		return length;
	}

	public static Path createPath(Multipolygon polygons,
			CoordinateTransformer mercator)
	{
		Path path = new Path();
		createPath(path, polygons, mercator);
		return path;
	}

	public static void createPath(Path path, Multipolygon polygons,
			CoordinateTransformer mercator)
	{
		int numPolygons = polygons.getNumberOfPolygons();
		for (int l = 0; l < numPolygons; l++) {
			Polygon polygon = polygons.getPolygon(l);

			LinearRing exterior = polygon.getExteriorRing();
			createPath(path, exterior, false, mercator);

			int numRings = polygon.getNumberOfInteriorRings();
			for (int k = 0; k < numRings; k++) {
				LinearRing interior = polygon.getInteriorRing(k);
				createPath(path, interior, false, mercator);
			}
		}
	}

	public static float[] createPointPath(Linestring string,
			CoordinateTransformer mercator)
	{
		int numPoints = string.getNumberOfCoordinates();
		float[] points = new float[numPoints * 2];

		float x = mercator.getX(string.x[0]);
		float y = mercator.getY(string.y[0]);
		points[0] = x;
		points[1] = y;

		for (int i = 1; i < numPoints; i++) {
			int p = i * 2;
			x = mercator.getX(string.x[i]);
			y = mercator.getY(string.y[i]);
			points[p] = x;
			points[p + 1] = y;
		}

		return points;
	}

	// // this method produces arrays usable with Canvas.drawLines
	// public static float[] createPointPath(Linestring string, Mercator
	// mercator) {
	// int length = string.x.length;
	// int entries = (length - 1) * 4;
	// float[] points = new float[entries];
	//
	// points[0] = mercator.getX(string.x[0]);
	// points[1] = mercator.getY(string.y[0]);
	//
	// int i = 1;
	// int k = 2;
	// for (i = 1; i < length - 1; i++) {
	// int x = mercator.getX(string.x[i]);
	// int y = mercator.getY(string.y[i]);
	// points[k] = points[k + 2] = x;
	// points[k + 1] = points[k + 3] = y;
	// k += 4;
	// }
	// points[k] = mercator.getX(string.x[i]);
	// points[k + 1] = mercator.getY(string.y[i]);
	// return points;
	// }

	public static float[][] createPointPaths(Polygon polygon,
			CoordinateTransformer mercator)
	{
		Linestring exterior = polygon.getExteriorRing();
		int numRings = polygon.getNumberOfInteriorRings();
		float[][] points = new float[numRings + 1][];
		points[0] = GeometryConversion.createPointPath(exterior, mercator);
		for (int k = 0; k < numRings; k++) {
			Linestring interior = polygon.getInteriorRing(k);
			points[k + 1] = GeometryConversion.createPointPath(interior,
					mercator);
		}
		return points;
	}

	public static float[][][] createPointPaths(Multipolygon multiPolygon,
			CoordinateTransformer mercator)
	{
		int numPolygons = multiPolygon.getNumberOfPolygons();
		float[][][] points = new float[numPolygons][][];
		for (int i = 0; i < numPolygons; i++) {
			Polygon polygon = multiPolygon.getPolygon(i);
			points[i] = GeometryConversion.createPointPaths(polygon, mercator);
		}
		return points;
	}

}
