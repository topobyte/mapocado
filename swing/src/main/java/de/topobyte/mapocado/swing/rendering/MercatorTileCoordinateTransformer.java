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
