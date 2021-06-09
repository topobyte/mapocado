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
