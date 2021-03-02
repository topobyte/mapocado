package de.topobyte.mapocado.mapformat.rtree.ram;

import java.io.Serializable;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;

public abstract class AbstractTreeElement implements ITreeElement, Serializable
{

	private static final long serialVersionUID = -629968485856735206L;

	private BoundingBox boundingBox;

	public AbstractTreeElement()
	{
		// serializable constructor
	}

	public AbstractTreeElement(BoundingBox boundingBox)
	{
		this.boundingBox = boundingBox;
	}

	@Override
	public BoundingBox getBoundingBox()
	{
		return boundingBox;
	}

	@Override
	public boolean intersects(BoundingBox queryBox)
	{
		return boundingBox.intersects(queryBox);
	}
}
