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

import de.topobyte.chromaticity.ColorCode;

public class Area extends AbstractElement
{

	private static final long serialVersionUID = 709016942331428724L;

	private final String source;
	private final ColorCode fill;
	private final ColorCode stroke;
	private final float strokeWidth;

	public Area(int level, String source, ColorCode fill, ColorCode stroke,
			float strokeWidth)
	{
		super(level);
		this.source = source;
		this.fill = fill;
		this.stroke = stroke;
		this.strokeWidth = strokeWidth;
	}

	public String getSource()
	{
		return source;
	}

	public ColorCode getFill()
	{
		return fill;
	}

	public ColorCode getStroke()
	{
		return stroke;
	}

	public float getStrokeWidth()
	{
		return strokeWidth;
	}

}
