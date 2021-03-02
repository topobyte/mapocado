package de.topobyte.mapocado.swing.rendering.items;

import java.awt.Shape;
import java.awt.geom.Point2D;

public interface RenderItem
{

	public Shape getShape();

	public Point2D getPoint();

	public boolean isClosed();
}
