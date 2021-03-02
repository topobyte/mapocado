package de.topobyte.mapocado.mapformat.geom;

public class Polygon
{

	private final LinearRing exteriorRing;
	private final LinearRing[] interiorRings;

	public Polygon(LinearRing exteriorRing, LinearRing[] interiorRings)
	{
		this.exteriorRing = exteriorRing;
		this.interiorRings = interiorRings;
	}

	public boolean isEmpty()
	{
		return exteriorRing.getNumberOfCoordinates() < 4;
	}

	public LinearRing getExteriorRing()
	{
		return exteriorRing;
	}

	public LinearRing[] getInteriorRings()
	{
		return interiorRings;
	}

	public int getNumberOfInteriorRings()
	{
		return interiorRings.length;
	}

	public LinearRing getInteriorRing(int i)
	{
		return interiorRings[i];
	}

	public int getNumberOfCoordinates()
	{
		int n = exteriorRing.getNumberOfCoordinates();
		for (int i = 0; i < interiorRings.length; i++) {
			n += interiorRings[i].getNumberOfCoordinates();
		}
		return n;
	}

}
