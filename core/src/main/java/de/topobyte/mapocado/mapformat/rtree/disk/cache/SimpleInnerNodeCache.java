package de.topobyte.mapocado.mapformat.rtree.disk.cache;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.rtree.disk.Node;

/**
 * A simple cache implementation that has separate caches for each depth of
 * nodes.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class SimpleInnerNodeCache implements InnerNodeCache
{

	List<Cache<Node>> caches = new ArrayList<>();

	@Override
	public Node get(int address, int depth)
	{
		Cache<Node> levelCache = getCache(depth);
		return levelCache.get(address);
	}

	@Override
	public void put(int address, int depth, Node node)
	{
		Cache<Node> levelCache = getCache(depth);
		levelCache.put(address, node);
	}

	/*
	 * Get, and possibly create if necessary, the cache for the specified depth.
	 */
	Cache<Node> getCache(int depth)
	{
		if (caches.size() < depth + 1) {
			createCachesUpTo(depth);
		}
		return caches.get(depth);
	}

	/*
	 * Get the number of levels currently providing a cache for.
	 */
	int getNumberOfCacheLevels()
	{
		return caches.size();
	}

	/*
	 * Create all caches that do not exist yet, up to the cache for elements of
	 * level 'depth'.
	 */
	private void createCachesUpTo(int depth)
	{
		int lastExisting = caches.size();
		for (int i = lastExisting; i <= depth; i++) {
			caches.add(new CacheImpl<Node>(getCacheSize(depth)));
		}
	}

	/*
	 * Get the cache size depending on the depth the cache is responsible for.
	 */
	private int getCacheSize(int depth)
	{
		int max = 256;
		int size = 1 << depth;
		size *= 8;
		return size > max ? max : size;
	}

}
