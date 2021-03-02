package de.topobyte.mapocado.mapformat.geom;

public class Linestring
{

	public int[] x;
	public int[] y;

	public Linestring(int length)
	{
		x = new int[length];
		y = new int[length];
	}

	public Linestring(int[] x, int[] y)
	{
		this.x = x;
		this.y = y;
	}

	public int getNumberOfCoordinates()
	{
		return x.length;
	}

	public boolean isClosed()
	{
		int last = x.length - 1;
		return x[0] == x[last] && y[0] == y[last];
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < x.length; i++) {
			builder.append(x[i]);
			builder.append(", ");
			builder.append(y[i]);
			builder.append(" ");
		}
		return builder.toString();
	}
}
