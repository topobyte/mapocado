package de.topobyte.mapocado.mapformat.rtree;

import java.io.Serializable;

import org.locationtech.jts.geom.Envelope;

import de.topobyte.mapocado.mapformat.Geo;

public class BoundingBox implements Serializable
{

	private static final long serialVersionUID = -7563196309451166349L;

	private int minX;
	private int maxX;
	private int minY;
	private int maxY;

	public BoundingBox(int minX, int maxX, int minY, int maxY)
	{
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		validate();
	}

	public BoundingBox(double minX, double maxX, double minY, double maxY,
			boolean convertToMercator)
	{
		if (convertToMercator) {
			this.minX = Geo.mercatorFromLongitude(minX);
			this.maxX = Geo.mercatorFromLongitude(maxX);
			this.minY = Geo.mercatorFromLatitude(minY);
			this.maxY = Geo.mercatorFromLatitude(maxY);
		} else {
			this.minX = (int) minX;
			this.maxX = (int) maxX;
			this.minY = (int) minY;
			this.maxY = (int) maxY;
		}
		validate();
	}

	public BoundingBox(Envelope envelope, boolean convertToMercator)
	{
		if (convertToMercator) {
			this.minX = Geo.mercatorFromLongitude(envelope.getMinX());
			this.maxX = Geo.mercatorFromLongitude(envelope.getMaxX());
			this.minY = Geo.mercatorFromLatitude(envelope.getMinY());
			this.maxY = Geo.mercatorFromLatitude(envelope.getMaxY());
		} else {
			this.minX = (int) envelope.getMinX();
			this.maxX = (int) envelope.getMaxX();
			this.minY = (int) envelope.getMinY();
			this.maxY = (int) envelope.getMaxY();
		}
		validate();
	}

	private void validate()
	{
		if (minX > maxX) {
			int tmp = minX;
			minX = maxX;
			maxX = tmp;
		}
		if (minY > maxY) {
			int tmp = minY;
			minY = maxY;
			maxY = tmp;
		}
	}

	public int getMinX()
	{
		return minX;
	}

	public int getMinY()
	{
		return minY;
	}

	public int getMaxX()
	{
		return maxX;
	}

	public int getMaxY()
	{
		return maxY;
	}

	public int getCenterX()
	{
		return (minX + maxX) / 2;
	}

	public int getCenterY()
	{
		return (minY + maxY) / 2;
	}

	public boolean intersects(BoundingBox other)
	{
		return maxX >= other.minX && minX <= other.maxX && maxY >= other.minY
				&& minY <= other.maxY;
	}

	public BoundingBox include(BoundingBox other)
	{
		BoundingBox result = new BoundingBox(minX, maxX, minY, maxY);
		if (other.minX < minX) {
			result.minX = other.minX;
		}
		if (other.maxX > maxX) {
			result.maxX = other.maxX;
		}
		if (other.minY < minY) {
			result.minY = other.minY;
		}
		if (other.maxY > maxY) {
			result.maxY = other.maxY;
		}
		return result;
	}

	@Override
	public String toString()
	{
		return minX + "," + maxX + ";" + minY + "," + maxY;
	}
}
