package de.topobyte.mapocado.mapformat.rtree.disk.cache;

public interface Cache<T>
{
	public T get(int key);

	public void put(int key, T object);
}
