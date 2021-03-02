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
