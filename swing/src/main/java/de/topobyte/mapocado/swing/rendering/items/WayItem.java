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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.topobyte.mapocado.mapformat.model.Way;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class WayItem implements RenderItem
{

	private final Path2D path;
	private final boolean closed;
	private final Way way;

	public WayItem(Way way, Path2D path, boolean closed)
	{
		this.way = way;
		this.path = path;
		this.closed = closed;
	}

	public Way getWay()
	{
		return way;
	}

	public Path2D getPath()
	{
		return path;
	}

	@Override
	public boolean isClosed()
	{
		return closed;
	}

	@Override
	public Shape getShape()
	{
		return path;
	}

	@Override
	public Point2D getPoint()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return String.format("WayItem closed? %b:%b, #points: %d", closed,
				way.getString().isClosed(), way.getString().x.length);
	}

}
