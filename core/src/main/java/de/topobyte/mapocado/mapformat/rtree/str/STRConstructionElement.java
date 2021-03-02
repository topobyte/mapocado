package de.topobyte.mapocado.mapformat.rtree.str;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;

public class STRConstructionElement<T>
{
	private final BoundingBox rect;
	private final T object;

	public STRConstructionElement(BoundingBox rect, T object)
	{
		this.rect = rect;
		this.object = object;
	}

	public BoundingBox getBoundingBox()
	{
		return rect;
	}

	public T getObject()
	{
		return object;
	}

}
