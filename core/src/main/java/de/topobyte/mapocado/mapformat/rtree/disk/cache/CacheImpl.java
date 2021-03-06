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

import java.util.Map;

public class CacheImpl<T> implements Cache<T>
{
	private final int size;

	private long hits = 0;
	private long misses = 0;

	Map<Integer, T> map = null;

	public CacheImpl(int size)
	{
		this.size = size;
		map = new LruLinkedHashMap<>(size);
	}

	@Override
	public T get(int key)
	{
		T object = map.get(key);
		if (object != null) {
			hits += 1;
		} else {
			misses += 1;
		}
		return object;
	}

	@Override
	public void put(int key, T object)
	{
		map.put(key, object);
	}

	public int getSize()
	{
		return size;
	}

	public long getHits()
	{
		return hits;
	}

	public long getMisses()
	{
		return misses;
	}

	public long getAccesses()
	{
		return hits + misses;
	}

	public double getHitRatio()
	{
		long access = hits + misses;
		if (access == 0) {
			return 1;
		}
		return hits / (double) access;
	}

	public double getMissRatio()
	{
		return 1 - getHitRatio();
	}
}
