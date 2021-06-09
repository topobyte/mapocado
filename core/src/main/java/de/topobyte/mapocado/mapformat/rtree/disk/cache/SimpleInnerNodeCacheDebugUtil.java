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
