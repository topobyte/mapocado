package de.topobyte.mapocado.mapformat.rtree.str;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;

public abstract class AbstractSTRTreeElement implements ITreeElement
{
	private BoundingBox boundingBox;

	public AbstractSTRTreeElement(BoundingBox boundingBox)
	{
		this.boundingBox = boundingBox;
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
