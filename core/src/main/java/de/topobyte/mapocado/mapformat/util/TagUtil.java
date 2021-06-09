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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.topobyte.mapocado.mapformat.io.StringPool;

public class TagUtil
{
	public static Map<Integer, String> convertTags(Map<String, String> tags,
			StringPool keepPool)
	{
		// only keep tags that may have to be stored because a keep tag
		HashMap<Integer, String> itags = new HashMap<>();
		for (Entry<String, String> entry : tags.entrySet()) {
			if (!keepPool.containsString(entry.getKey())) {
				continue;
			}
			int key = keepPool.getId(entry.getKey());
			itags.put(key, entry.getValue());
		}
		return itags;
	}
}
