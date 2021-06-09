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

public class Linestring
{

	public int[] x;
	public int[] y;

	public Linestring(int length)
	{
		x = new int[length];
		y = new int[length];
	}

	public Linestring(int[] x, int[] y)
	{
		this.x = x;
		this.y = y;
	}

	public int getNumberOfCoordinates()
	{
		return x.length;
	}

	public boolean isClosed()
	{
		int last = x.length - 1;
		return x[0] == x[last] && y[0] == y[last];
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < x.length; i++) {
			builder.append(x[i]);
			builder.append(", ");
			builder.append(y[i]);
			builder.append(" ");
		}
		return builder.toString();
	}
}
