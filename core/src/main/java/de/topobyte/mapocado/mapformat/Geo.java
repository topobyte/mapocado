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
