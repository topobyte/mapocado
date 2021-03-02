package de.topobyte.mapocado.swing.rendering.pathtext;

import java.awt.geom.GeneralPath;

public class TextPath
{

	private final GeneralPath path;
	private final float startX, startY, endX, endY;

	public TextPath(GeneralPath path, float startX, float startY, float endX,
			float endY)
	{
		this.path = path;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public GeneralPath getPath()
	{
		return path;
	}

	public float getStartX()
	{
		return startX;
	}

	public float getStartY()
	{
		return startY;
	}

	public float getEndX()
	{
		return endX;
	}

	public float getEndY()
	{
		return endY;
	}

	public boolean isReverseX()
	{
		return endX < startX;
	}

}
