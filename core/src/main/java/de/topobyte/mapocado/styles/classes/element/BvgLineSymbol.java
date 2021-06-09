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

package de.topobyte.mapocado.styles.classes.element;

public class BvgLineSymbol extends LineSymbol
{

	private static final long serialVersionUID = -3280221709806087806L;

	private float width;

	public BvgLineSymbol(int level, String source, float width, float offset,
			boolean repeat, float repeatDistance)
	{
		super(level, source, offset, repeat, repeatDistance);
		this.width = width;
	}

	public float getWidth()
	{
		return width;
	}

}