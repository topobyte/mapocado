package de.topobyte.mapocado.mapformat.rtree.disk.cache;

import de.topobyte.mapocado.mapformat.rtree.disk.Node;

public interface InnerNodeCache
{
	/**
	 * Retrieve the node at the specified address from the cache if available.
	 * 
	 * @param address
	 *            the address this node is at in the original file.
	 * @param depth
	 *            the depth of the node within the tree.
	 * @return the node from cache or null if not available.
	 */
	public Node get(int address, int depth);

	/**
	 * Tell the cache that the specified element has been retrieved from the
	 * underlying file. This method allows the cache to insert elements into its
	 * internal structure.
	 * 
	 * @param address
	 *            the address this node is at in the original file.
	 * @param node
	 *            the node to store.
	 * @param depth
	 *            the depth of the node within the tree.
	 * 
	 */
	public void put(int address, int depth, Node node);
}
