package de.topobyte.mapocado.mapformat.rtree.disk;

public interface TraversalHandler<T>
{

	public void handleInnerNode(Entry entry, Node node, int level);

	public void handleLeaf(Entry entry, Node leaf, int level);

	public void handleEntry(Entry entry, T thing, int level);

}
