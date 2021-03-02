package de.topobyte.mapocado.mapformat.rtree.str;

import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;

public class STRTree<T> implements IRTree<T>
{

	private final ITreeElement root;
	private final int height;

	public STRTree(ITreeElement root, int height)
	{
		this.root = root;
		this.height = height;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public ITreeElement getRoot()
	{
		return root;
	}

}
