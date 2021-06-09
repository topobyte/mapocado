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

package de.topobyte.mapocado.swing.rendering.items;

import java.awt.Shape;
import java.awt.geom.Point2D;

public class NodeItem implements RenderItem
{
	private final float x;
	private final float y;

	public NodeItem(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public Shape getShape()
	{
		return null;
	}

	@Override
	public boolean isClosed()
	{
		return false;
	}

	@Override
	public Point2D getPoint()
	{
		return new Point2D.Double(x, y);
	}

}
