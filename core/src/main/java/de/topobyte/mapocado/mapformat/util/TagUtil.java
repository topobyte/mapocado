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
