package de.topobyte.mapocado.mapformat.rtree.ram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeNode;

public class TreeNode extends AbstractTreeElement
		implements ITreeNode, Serializable
{

	private static final long serialVersionUID = 7746644577163505161L;

	private List<ITreeElement> children = new ArrayList<>();

	public TreeNode()
	{
		// serializable constructor
		super();
	}

	public TreeNode(BoundingBox box)
	{
		super(box);
	}

	@Override
	public List<ITreeElement> getChildren()
	{
		return children;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	public boolean isInner()
	{
		return true;
	}
}
