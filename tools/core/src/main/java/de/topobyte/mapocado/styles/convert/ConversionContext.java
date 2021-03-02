package de.topobyte.mapocado.styles.convert;

import java.util.HashMap;
import java.util.Map;

public class ConversionContext
{

	private Map<String, Object> storage = new HashMap<>();

	public void store(String key, Object object)
	{
		storage.put(key, object);
	}

	public Object load(String key)
	{
		return storage.get(key);
	}
}
