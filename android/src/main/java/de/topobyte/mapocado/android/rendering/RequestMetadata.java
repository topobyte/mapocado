package de.topobyte.mapocado.android.rendering;

import de.topobyte.adt.geo.BBox;

public class RequestMetadata
{

	private final BBox bbox;
	private final int zoom;

	public RequestMetadata(BBox bbox, int zoom)
	{
		this.bbox = bbox;
		this.zoom = zoom;
	}

	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof RequestMetadata)) {
			return false;
		}

		RequestMetadata other = (RequestMetadata) o;
		return other.zoom == zoom && other.bbox.getLat1() == bbox.getLat1()
				&& other.bbox.getLat2() == bbox.getLat2()
				&& other.bbox.getLon1() == bbox.getLon1()
				&& other.bbox.getLon2() == bbox.getLon2();
	}

	@Override
	public int hashCode()
	{
		return (int) (zoom + Double.doubleToLongBits(bbox.getLat1())
				+ Double.doubleToLongBits(bbox.getLat2())
				+ Double.doubleToLongBits(bbox.getLon1())
				+ Double.doubleToLongBits(bbox.getLon2()));
	}

}
