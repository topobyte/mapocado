package de.topobyte.mapocado.mapformat.rtree.disk.cache;

import de.topobyte.mapocado.mapformat.rtree.disk.Node;

/**
 * A cache implementation that doesn't cache anything. This is a dummy to
 * disable cacheing.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class DummyInnerNodeCache implements InnerNodeCache
{

	@Override
	public Node get(int address, int depth)
	{
		return null;
	}

	@Override
	public void put(int address, int depth, Node node)
	{
		// nop
	}

}
