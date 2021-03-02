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
