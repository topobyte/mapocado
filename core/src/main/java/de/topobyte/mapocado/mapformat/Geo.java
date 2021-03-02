package de.topobyte.mapocado.mapformat;

import de.topobyte.geomath.WGS84;

public class Geo
{
	public static final int MERCATOR_SHIFT = 26;
	public static final int MERCATOR_SIZE = 1 << MERCATOR_SHIFT;

	public static int mercatorFromLongitude(double lon)
	{
		double mx = WGS84.lon2merc(lon, MERCATOR_SIZE);
		return (int) Math.round(mx);
	}

	public static int mercatorFromLatitude(double lat)
	{
		double my = WGS84.lat2merc(lat, MERCATOR_SIZE);
		return (int) Math.round(my);
	}

	public static int mercatorFromLongitudeCut(double lon)
	{
		double mx = WGS84.lon2merc(lon, MERCATOR_SIZE);
		return (int) mx;
	}

	public static int mercatorFromLatitudeCut(double lat)
	{
		double my = WGS84.lat2merc(lat, MERCATOR_SIZE);
		return (int) my;
	}
}
