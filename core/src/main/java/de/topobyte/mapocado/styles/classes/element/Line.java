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

import java.util.List;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.misc.enums.CapType;

public class Line extends AbstractElement
{

	private static final long serialVersionUID = 3653466892877253022L;

	private final ColorCode stroke;
	private final float strokeWidth;
	private final CapType capType;
	private final List<Float> dashArray;

	public Line(int level, ColorCode stroke, float strokeWidth, CapType capType,
			List<Float> dashArray)
	{
		super(level);
		this.stroke = stroke;
		this.strokeWidth = strokeWidth;
		this.capType = capType;
		this.dashArray = dashArray;
	}

	public ColorCode getStroke()
	{
		return stroke;
	}

	public float getStrokeWidth()
	{
		return strokeWidth;
	}

	public CapType getCapType()
	{
		return capType;
	}

	public List<Float> getDashArray()
	{
		return dashArray;
	}
}
