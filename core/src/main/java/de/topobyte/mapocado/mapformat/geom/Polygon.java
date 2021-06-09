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

package de.topobyte.mapocado.mapformat.geom;

public class Polygon
{

	private final LinearRing exteriorRing;
	private final LinearRing[] interiorRings;

	public Polygon(LinearRing exteriorRing, LinearRing[] interiorRings)
	{
		this.exteriorRing = exteriorRing;
		this.interiorRings = interiorRings;
	}

	public boolean isEmpty()
	{
		return exteriorRing.getNumberOfCoordinates() < 4;
	}

	public LinearRing getExteriorRing()
	{
		return exteriorRing;
	}

	public LinearRing[] getInteriorRings()
	{
		return interiorRings;
	}

	public int getNumberOfInteriorRings()
	{
		return interiorRings.length;
	}

	public LinearRing getInteriorRing(int i)
	{
		return interiorRings[i];
	}

	public int getNumberOfCoordinates()
	{
		int n = exteriorRing.getNumberOfCoordinates();
		for (int i = 0; i < interiorRings.length; i++) {
			n += interiorRings[i].getNumberOfCoordinates();
		}
		return n;
	}

}
