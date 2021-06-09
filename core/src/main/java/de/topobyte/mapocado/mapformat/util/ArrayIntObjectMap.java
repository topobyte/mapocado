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
