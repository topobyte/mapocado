package de.topobyte.mapocado.mapformat.rtree.str;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;

public class STREntry<T> extends AbstractSTRTreeElement implements ITreeEntry<T>
{

	private final T object;

	public STREntry(BoundingBox box, T object)
	{
		super(box);
		this.object = object;
	}

	@Override
	public boolean isInner()
	{
		return false;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	public T getThing()
	{
		return object;
	}

}
