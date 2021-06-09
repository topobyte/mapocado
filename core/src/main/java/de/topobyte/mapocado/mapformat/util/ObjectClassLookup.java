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

package de.topobyte.mapocado.mapformat.util;

import java.util.List;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;

public class ObjectClassLookup
{

	private TIntObjectHashMap<ObjectClassRef> lookup = new TIntObjectHashMap<>();

	public ObjectClassLookup(List<ObjectClassRef> objectClassRefs,
			StringPool refPool)
	{
		for (ObjectClassRef classRef : objectClassRefs) {
			String refName = classRef.getRef();
			int refId = refPool.getId(refName);
			lookup.put(refId, classRef);
		}
	}

	public ObjectClassRef get(int refId)
	{
		return lookup.get(refId);
	}

}
