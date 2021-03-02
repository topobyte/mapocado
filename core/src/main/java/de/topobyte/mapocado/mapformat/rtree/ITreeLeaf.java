package de.topobyte.mapocado.mapformat.rtree;

import java.util.List;

public interface ITreeLeaf<T> extends ITreeElement
{

	public List<? extends ITreeEntry<T>> getChildren();

}
