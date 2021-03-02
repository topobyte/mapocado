package de.topobyte.mapocado.mapformat.rtree.str;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeNode;

public class STRNode extends AbstractSTRTreeElement implements ITreeNode
{

	List<ITreeElement> childs = new ArrayList<>();

	public STRNode(List<ITreeElement> children)
	{
		super(createBox(children));
		childs.addAll(children);
	}

	private static <T> BoundingBox createBox(List<ITreeElement> children)
	{
		BoundingBox box = children.get(0).getBoundingBox();
		for (int i = 1; i < children.size(); i++) {
			ITreeElement element = children.get(i);
			box = box.include(element.getBoundingBox());
		}
		return box;
	}

	@Override
	public boolean isInner()
	{
		return true;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	public List<ITreeElement> getChildren()
	{
		return childs;
	}
}
