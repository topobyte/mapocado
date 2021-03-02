package de.topobyte.mapocado.mapformat;

import de.topobyte.jgs.transform.CoordinateTransformer;

public class ToMercatorTransformer implements CoordinateTransformer
{

	@Override
	public double getX(double lon)
	{
		return Geo.mercatorFromLongitude(lon);
	}

	@Override
	public double getY(double lat)
	{
		return Geo.mercatorFromLatitude(lat);
	}

}
