package de.topobyte.mapocado.mapformat.rtree.ram;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;

public class TreeEntry<T> extends AbstractTreeElement implements ITreeEntry<T>
{

	private static final long serialVersionUID = 3508733036787176054L;

	private final T thing;

	public TreeEntry(BoundingBox boundingBox, T thing)
	{
		super(boundingBox);
		this.thing = thing;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	public boolean isInner()
	{
		return false;
	}

	@Override
	public T getThing()
	{
		return thing;
	}

}
