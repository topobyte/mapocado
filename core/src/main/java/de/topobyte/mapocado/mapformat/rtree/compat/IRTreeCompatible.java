package de.topobyte.mapocado.mapformat.rtree.compat;

import java.io.IOException;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.IRTree;

public interface IRTreeCompatible<T>
{

	public IRTree<T> createIRTree() throws IOException;

	public void add(BoundingBox rect, T object);

}
