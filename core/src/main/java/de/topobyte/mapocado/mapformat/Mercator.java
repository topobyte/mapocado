package de.topobyte.mapocado.mapformat;

import de.topobyte.jeography.core.Tile;

public class Mercator implements LengthTransformer
{

	private int zoom, tx, ty;

	private double mult = 1;

	private int targetTileSize;

	public Mercator(Tile tile, int targetTileSize)
	{
		zoom = tile.getZoom();
		tx = tile.getTx();
		ty = tile.getTy();

		this.targetTileSize = targetTileSize;

		int shift = Geo.MERCATOR_SHIFT - zoom;
		if (shift < 0) {
			shift *= -1;
			mult = 1 << shift;
		} else {
			mult = 1.0 / (1 << shift);
		}
	}

	/**
	 * Convert from storage mercator to tile+zoom mercator.
	 */
	@Override
	public float getX(int mx)
	{
		double smx = mx * mult;
		return (float) ((smx - tx) * targetTileSize);
	}

	/**
	 * Convert from storage mercator to tile+zoom mercator.
	 */
	@Override
	public float getY(int my)
	{
		double smy = my * mult;
		return (float) ((smy - ty) * targetTileSize);
	}

	/**
	 * Convert from storage mercator to target zoom mercator.
	 */
	public static int getX(int mx, int targetZoom)
	{
		int shift = Geo.MERCATOR_SHIFT - targetZoom - 8;
		boolean inverse = false;
		if (shift < 0) {
			shift *= -1;
			inverse = true;
		}
		int smx = inverse ? mx << shift : mx >> shift;
		return smx;
	}

	public static double getX(int mx, double targetZoom)
	{
		double shift = Geo.MERCATOR_SHIFT - targetZoom - 8;
		double factor = Math.pow(2, shift);
		return mx / factor;
	}

	/**
	 * Convert from storage mercator to target zoom mercator.
	 */
	public static int getY(int my, int targetZoom)
	{
		int shift = Geo.MERCATOR_SHIFT - targetZoom - 8;
		boolean inverse = false;
		if (shift < 0) {
			shift *= -1;
			inverse = true;
		}
		int smy = inverse ? my << shift : my >> shift;
		return smy;
	}

	public static double getY(int my, double targetZoom)
	{
		double shift = Geo.MERCATOR_SHIFT - targetZoom - 8;
		double factor = Math.pow(2, shift);
		return my / factor;
	}

	/**
	 * Convert a length in mercator units in the specified zoom level to a
	 * length in mercator units in the storage zoomlevel
	 * 
	 * @return the length in storage mercator units.
	 */
	@Override
	public double getLengthStorageUnits(double length, int zoom)
	{
		length = length / targetTileSize * Tile.SIZE;
		int dz = Geo.MERCATOR_SHIFT - zoom - 8;
		boolean inverse = false;
		if (dz < 0) {
			dz *= -1;
			inverse = true;
		}
		int scale = 1 << dz;
		double len = inverse ? length / scale : length * scale;
		return len;
	}

	/**
	 * Convert a length in storage units to a length in mercator units in the
	 * specified zoom level
	 * 
	 * @return the length in mercator units.
	 */
	@Override
	public double getLengthTileUnits(double length, int zoom)
	{
		length = length * targetTileSize / Tile.SIZE;
		int dz = Geo.MERCATOR_SHIFT - zoom - 8;
		boolean inverse = false;
		if (dz < 0) {
			dz *= -1;
			inverse = true;
		}
		int scale = 1 << dz;
		double len = inverse ? length * scale : length / scale;
		return len;
	}

	/**
	 * Static version of the above method that is useful in circumstances where
	 * no targetTileSize is defined.
	 */
	public static double getLengthStorageUnitsDefault(double length, int zoom)
	{
		int dz = Geo.MERCATOR_SHIFT - zoom - 8;
		boolean inverse = false;
		if (dz < 0) {
			dz *= -1;
			inverse = true;
		}
		int scale = 1 << dz;
		double len = inverse ? length / scale : length * scale;
		return len;
	}

	/**
	 * Static version of the above method that is useful in circumstances where
	 * no targetTileSize is defined.
	 */
	public static double getLengthTileUnitsDefault(double length, int zoom)
	{
		int dz = Geo.MERCATOR_SHIFT - zoom - 8;
		boolean inverse = false;
		if (dz < 0) {
			dz *= -1;
			inverse = true;
		}
		int scale = 1 << dz;
		double len = inverse ? length * scale : length / scale;
		return len;
	}

	/**
	 * Convert a tile x coordinate to the storage mercator
	 */
	public static int getStorageX(int mx, int zoom)
	{
		int shift = Geo.MERCATOR_SHIFT - zoom;
		boolean inverse = false;
		if (shift < 0) {
			shift *= -1;
			inverse = true;
		}
		int smx = inverse ? mx >> shift : mx << shift;
		return smx;
	}

	/**
	 * Convert a tile y coordinate to the storage mercator
	 */
	public static int getStorageY(int my, int zoom)
	{
		int shift = Geo.MERCATOR_SHIFT - zoom;
		boolean inverse = false;
		if (shift < 0) {
			shift *= -1;
			inverse = true;
		}
		int smx = inverse ? my >> shift : my << shift;
		return smx;
	}
}
