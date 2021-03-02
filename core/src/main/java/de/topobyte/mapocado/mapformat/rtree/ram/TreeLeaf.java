package de.topobyte.mapocado.mapformat.rtree.ram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;
import de.topobyte.mapocado.mapformat.rtree.ITreeLeaf;

public class TreeLeaf<T> extends AbstractTreeElement
		implements ITreeLeaf<T>, Serializable
{

	private static final long serialVersionUID = -9122375943390815134L;

	private List<TreeEntry<T>> children = new ArrayList<>();

	public TreeLeaf()
	{
		// serializable constructor
		super();
	}

	public TreeLeaf(BoundingBox box)
	{
		super(box);
	}

	@Override
	public boolean isLeaf()
	{
		return true;
	}

	@Override
	public boolean isInner()
	{
		return false;
	}

	@Override
	public List<? extends ITreeEntry<T>> getChildren()
	{
		return children;
	}

}
