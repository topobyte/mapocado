package de.topobyte.mapocado.mapformat.util;

import java.util.ArrayList;

public class ArrayIntObjectMap<T>
{

	private ArrayList<T> storage = new ArrayList<>();

	public void put(int key, T value)
	{
		int size = storage.size();
		if (size <= key) {
			int add = key - size + 1;
			for (int i = 0; i < add; i++) {
				storage.add(null);
			}
		}
		storage.set(key, value);
	}

	public T get(int key)
	{
		return storage.get(key);
	}

}
