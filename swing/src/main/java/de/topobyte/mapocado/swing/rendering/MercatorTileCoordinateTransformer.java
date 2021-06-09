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

package de.topobyte.mapocado.swing.rendering;

import de.topobyte.jeography.core.Tile;
import de.topobyte.jgs.transform.CoordinateTransformer;
import de.topobyte.mapocado.mapformat.Geo;

public class MercatorTileCoordinateTransformer implements CoordinateTransformer
{

	private final Tile tile;

	public MercatorTileCoordinateTransformer(Tile tile)
	{
		this.tile = tile;
	}

	@Override
	public double getX(double dmx)
	{
		int mx = (int) dmx;
		int shift = Geo.MERCATOR_SHIFT - tile.getZoom() - 8;
		int smx = mx >> shift;

		int sub = tile.getTx() << 8;
		int rx = smx - sub;

		return rx;
	}

	@Override
	public double getY(double dmy)
	{
		int my = (int) dmy;
		int shift = Geo.MERCATOR_SHIFT - tile.getZoom() - 8;
		int smy = my >> shift;

		int sub = tile.getTy() << 8;
		int ry = smy - sub;

		return ry;
	}

}
