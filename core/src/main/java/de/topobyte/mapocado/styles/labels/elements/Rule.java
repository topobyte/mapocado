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

package de.topobyte.mapocado.styles.labels.elements;

public class Rule
{

	private int minZoom = 0;
	private int maxZoom = 0;
	private String key = null;

	public Rule()
	{
		// empty
	}

	public Rule(Rule rule)
	{
		minZoom = rule.minZoom;
		maxZoom = rule.maxZoom;
		key = rule.key;
	}

	public int getMinZoom()
	{
		return minZoom;
	}

	public void setMinZoom(int minZoom)
	{
		this.minZoom = minZoom;
	}

	public int getMaxZoom()
	{
		return maxZoom;
	}

	public void setMaxZoom(int maxZoom)
	{
		this.maxZoom = maxZoom;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

}
