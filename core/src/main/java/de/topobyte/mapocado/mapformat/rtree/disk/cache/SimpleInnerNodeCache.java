// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

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
