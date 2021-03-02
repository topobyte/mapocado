package de.topobyte.mapocado.mapformat;

public interface LengthTransformer extends CoordinateTransformer
{
	public double getLengthStorageUnits(double length, int zoom);

	public double getLengthTileUnits(double length, int zoom);
}
