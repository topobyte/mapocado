package de.topobyte.mapocado.mapformat.rtree;

import java.util.List;

public interface ITreeNode extends ITreeElement
{

	public List<ITreeElement> getChildren();

}
