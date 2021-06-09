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
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import de.topobyte.mapocado.mapformat.model.Relation;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class RelationItem implements RenderItem
{

	private final Area area;
	private final Relation relation;

	public RelationItem(Relation relation, Area area)
	{
		this.relation = relation;
		this.area = area;
	}

	public Relation getRelation()
	{
		return relation;
	}

	public Area getArea()
	{
		return area;
	}

	@Override
	public Shape getShape()
	{
		return area;
	}

	@Override
	public boolean isClosed()
	{
		return true;
	}

	@Override
	public Point2D getPoint()
	{
		return null;
	}
}
