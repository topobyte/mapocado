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

public class Circle extends AbstractElement
{
	private static final long serialVersionUID = 266124950099399277L;

	private final float radius;
	private final boolean scaleRadius;
	private final ColorCode fill;
	private final ColorCode stroke;
	private final float strokeWidth;

	public Circle(int level, float radius, boolean scaleRadius, ColorCode fill,
			ColorCode stroke, float strokeWidth)
	{
		super(level);
		this.radius = radius;
		this.scaleRadius = scaleRadius;
		this.fill = fill;
		this.stroke = stroke;
		this.strokeWidth = strokeWidth;

	}

	public float getRadius()
	{
		return radius;
	}

	public boolean isScaleRadius()
	{
		return scaleRadius;
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
