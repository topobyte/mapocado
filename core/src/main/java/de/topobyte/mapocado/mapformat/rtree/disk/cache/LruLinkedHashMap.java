package de.topobyte.mapocado.mapformat.rtree.disk.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruLinkedHashMap<K, V> extends LinkedHashMap<K, V>
{

	private static final long serialVersionUID = 2638533233463973318L;

	private final int size;

	public LruLinkedHashMap(int size)
	{
		super(size + 1, 0.75f, true);
		this.size = size;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
	{
		return size() > size;
	}

}
