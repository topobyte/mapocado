package de.topobyte.mapocado.mapformat.rtree.disk;

import java.io.IOException;
import java.util.List;

/**
 * A node may be an inner node or a leaf. An inner node contains just nodes or
 * just leafs. Leafs contains just elements. The isLeaf property distinguishes
 * nodes from leafs.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class Node
{

	final boolean isLeaf;
	final List<Entry> entries;

	public Node(boolean isLeaf, List<Entry> entries) throws IOException
	{
		this.isLeaf = isLeaf;
		this.entries = entries;
	}

	public List<Entry> getEntries()
	{
		return entries;
	}

}