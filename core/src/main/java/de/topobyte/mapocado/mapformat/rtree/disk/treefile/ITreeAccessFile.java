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

package de.topobyte.mapocado.mapformat.rtree.disk.treefile;

import java.io.IOException;

public interface ITreeAccessFile
{
	/*
	 * positioning methods
	 */

	public void seek(long i) throws IOException;

	public long getFilePointer() throws IOException;

	/*
	 * primitive data access
	 */

	public int read() throws IOException;

	public float readFloat() throws IOException;

	public int readInt() throws IOException;

	public int readUnsignedByte() throws IOException;

	public void readFully(byte[] buffer) throws IOException;

	/*
	 * CompactReader style access
	 */

	public int readVariableLengthUnsignedInteger() throws IOException;
}
