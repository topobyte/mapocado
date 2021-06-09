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

package de.topobyte.mapocado.android.rendering.gather;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.geom.Coordinate;

public class NodeData
{

	private final Coordinate coordinate;
	private boolean hasSymbol;
	private TIntObjectHashMap<String> tags = new TIntObjectHashMap<>();

	public NodeData(Coordinate coordinate, boolean hasSymbol,
			TIntObjectHashMap<String> tags)
	{
		this.coordinate = coordinate;
		this.hasSymbol = hasSymbol;
		this.tags = tags;
	}

	public Coordinate getPoint()
	{
		return coordinate;
	}

	public boolean hasSymbol()
	{
		return hasSymbol;
	}

	public void setHasSymbol(boolean hasSymbol)
	{
		this.hasSymbol = hasSymbol;
	}

	public TIntObjectHashMap<String> getTags()
	{
		return tags;
	}

}
