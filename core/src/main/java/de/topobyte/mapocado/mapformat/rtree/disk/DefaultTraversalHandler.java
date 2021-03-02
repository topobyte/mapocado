package de.topobyte.mapocado.mapformat.rtree.disk;

public class DefaultTraversalHandler<T> implements TraversalHandler<T>
{

	@Override
	public void handleInnerNode(Entry entry, Node node, int level)
	{
		// ignore
	}

	@Override
	public void handleLeaf(Entry entry, Node leaf, int level)
	{
		// ignore
	}

	@Override
	public void handleEntry(Entry entry, T thing, int level)
	{
		// ignore
	}

}
