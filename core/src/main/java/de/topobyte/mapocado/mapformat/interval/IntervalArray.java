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

package de.topobyte.mapocado.mapformat.interval;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import de.topobyte.randomaccess.FileAccess;

public class IntervalArray extends ArrayList<Integer>
{

	private static final long serialVersionUID = 142959554037998204L;

	public IntervalArray()
	{
		super();
	}

	public IntervalArray(Integer... initialElements)
	{
		super();
		for (Integer element : initialElements) {
			add(element);
		}
	}

	public int read(FileAccess file) throws IOException, ClassNotFoundException
	{
		long startPosition = file.getFilePointer();
		int numIntervals = file.readUnsignedByte();
		// System.out.println("intervals: " + numIntervals);
		for (int i = 0; i < numIntervals; i++) {
			int interval = file.readUnsignedByte();
			add(interval);
		}
		return (int) (file.getFilePointer() - startPosition);
	}

	public int write(RandomAccessFile file) throws IOException
	{
		file.writeByte(size());
		for (int i = 0; i < size(); i++) {
			file.writeByte(get(i));
		}
		return 1 + size();
	}
}
