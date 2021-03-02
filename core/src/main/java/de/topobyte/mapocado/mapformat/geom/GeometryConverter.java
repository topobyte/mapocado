package de.topobyte.mapocado.mapformat.geom;

import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;

import de.topobyte.jts.utils.transform.CoordinateGeometryTransformer;
import de.topobyte.mapocado.mapformat.Geo;
import de.topobyte.mapocado.mapformat.ToMercatorTransformer;

/**
 * Methods for converting lon/lat coded JTS geometries to Mercator coded
 * mapformat geometry instances.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class GeometryConverter
{

	public static Coordinate convert(Point point)
	{
		int mx = Geo.mercatorFromLongitude(point.getX());
		int my = Geo.mercatorFromLatitude(point.getY());
		Coordinate coordinate = new Coordinate(mx, my);
		return coordinate;
	}

	public static Linestring convert(LineString string)
	{
		CoordinateGeometryTransformer transformer = new CoordinateGeometryTransformer(
				new ToMercatorTransformer());
		LineString mercString = (LineString) transformer.transform(string);

		int nPoints = mercString.getNumPoints();
		Linestring line = new Linestring(nPoints);
		for (int i = 0; i < nPoints; i++) {
			org.locationtech.jts.geom.Coordinate cn = mercString
					.getCoordinateN(i);
			line.x[i] = (int) Math.round(cn.x);
			line.y[i] = (int) Math.round(cn.y);
		}
		return line;
	}

	public static LinearRing convertRing(LineString string)
	{
		CoordinateGeometryTransformer transformer = new CoordinateGeometryTransformer(
				new ToMercatorTransformer());
		LineString mercString = (LineString) transformer.transform(string);

		int nPoints = mercString.getNumPoints();
		LinearRing ring = new LinearRing(nPoints);
		for (int i = 0; i < nPoints; i++) {
			org.locationtech.jts.geom.Coordinate cn = mercString
					.getCoordinateN(i);
			ring.x[i] = (int) Math.round(cn.x);
			ring.y[i] = (int) Math.round(cn.y);
		}
		return ring;
	}

	public static Polygon convert(org.locationtech.jts.geom.Polygon polygon)
	{
		LineString exterior = polygon.getExteriorRing();
		LinearRing mercExterior = convertRing(exterior);
		int num = polygon.getNumInteriorRing();
		LinearRing[] interiors = new LinearRing[num];
		for (int i = 0; i < num; i++) {
			LineString interior = polygon.getInteriorRingN(i);
			interiors[i] = convertRing(interior);
		}
		return new Polygon(mercExterior, interiors);
	}

	public static Multipolygon convert(MultiPolygon multiPolygon)
	{
		int num = multiPolygon.getNumGeometries();
		Polygon[] polygons = new Polygon[num];
		for (int i = 0; i < num; i++) {
			org.locationtech.jts.geom.Polygon polygon = (org.locationtech.jts.geom.Polygon) multiPolygon
					.getGeometryN(i);
			polygons[i] = convert(polygon);
		}
		return new Multipolygon(polygons);
	}
}
