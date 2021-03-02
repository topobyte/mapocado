package de.topobyte.mapocado.mapformat.rtree.disk;

import de.topobyte.mapocado.mapformat.model.Byteable;

public interface ElementCallback<T extends Byteable>
{

	public void handle(T element);

}
