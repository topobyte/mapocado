package de.topobyte.mapocado.mapformat.geom;

public class Multipolygon
{

	private final Polygon[] polygons;

	public Multipolygon(Polygon[] polygons)
	{
		this.polygons = polygons;
	}

	public Polygon[] getPolygons()
	{
		return polygons;
	}

	public int getNumberOfPolygons()
	{
		return polygons.length;
	}

	public Polygon getPolygon(int i)
	{
		return polygons[i];
	}

	public int getNumberOfCoordinates()
	{
		int n = 0;
		for (Polygon polygon : polygons) {
			n += polygon.getNumberOfCoordinates();
		}
		return n;
	}
}
