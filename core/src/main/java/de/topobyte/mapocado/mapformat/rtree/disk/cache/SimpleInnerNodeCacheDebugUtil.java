package de.topobyte.mapocado.mapformat.rtree.disk.cache;

import de.topobyte.mapocado.mapformat.rtree.disk.Node;

public class SimpleInnerNodeCacheDebugUtil
{

	public static void printDebug(SimpleInnerNodeCache sinc)
	{
		int levels = sinc.getNumberOfCacheLevels();
		for (int i = 0; i < levels; i++) {
			Cache<Node> cache = sinc.getCache(i);
			if (cache instanceof CacheImpl) {
				CacheImpl<Node> c = (CacheImpl<Node>) cache;
				System.out.println(String.format(
						"hit: %.2f, miss: %.2f, size: %d, accesses: %d",
						c.getHitRatio(), c.getMissRatio(), c.getSize(),
						c.getAccesses()));
			}
		}
	}

}
