package de.topobyte.mapocado.mapformat.geom;

public class Coordinate
{

	private final int x;
	private final int y;

	public Coordinate(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	@Override
	public String toString()
	{
		return x + ", " + y;
	}
}
