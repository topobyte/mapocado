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
