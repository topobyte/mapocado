package de.topobyte.mapocado.mapformat.rtree.disk;

import de.topobyte.mapocado.mapformat.model.Byteable;
import de.topobyte.mapocado.mapformat.rtree.disk.cache.InnerNodeCache;
import de.topobyte.mapocado.mapformat.rtree.disk.cache.SimpleInnerNodeCache;
import de.topobyte.mapocado.mapformat.rtree.disk.cache.SimpleInnerNodeCacheDebugUtil;

public class DiskTreeDebugUtil
{

	public static <T extends Byteable> void printCacheDebug(DiskTree<T> tree)
	{
		InnerNodeCache cache = tree.getInnerNodeCache();

		if (cache instanceof SimpleInnerNodeCache) {
			SimpleInnerNodeCache sinc = (SimpleInnerNodeCache) cache;
			SimpleInnerNodeCacheDebugUtil.printDebug(sinc);
		} else {
			System.out.println(
					"no debug implemented for this type of cache: " + cache);
		}
	}

}
