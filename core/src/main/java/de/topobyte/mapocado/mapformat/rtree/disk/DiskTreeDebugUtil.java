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
