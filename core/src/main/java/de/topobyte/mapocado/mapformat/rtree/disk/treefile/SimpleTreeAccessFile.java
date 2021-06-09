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
import java.io.InputStream;

import de.topobyte.mapocado.mapformat.util.CompactReaderInputStream;
import de.topobyte.randomaccess.FileAccess;

public class SimpleTreeAccessFile implements ITreeAccessFile
{

	private final FileAccess file;

	public SimpleTreeAccessFile(FileAccess file)
	{
		this.file = file;
	}

	/*
	 * positioning methods
	 */

	@Override
	public void seek(long i) throws IOException
	{
		long filePointer = getFilePointer();
		if (i != filePointer) {
			// seekRandomAccessFile
			file.seek(i);
			// long diff = i - filePointer;
			// System.out.println("jump offset: " + diff);
		}
	}

	@Override
	public long getFilePointer() throws IOException
	{
		return file.getFilePointer();
	}

	/*
	 * primitive data access
	 */

	@Override
	public int read() throws IOException
	{
		return file.read();
	}

	@Override
	public float readFloat() throws IOException
	{
		return file.readFloat();
	}

	@Override
	public int readInt() throws IOException
	{
		return file.readInt();
	}

	@Override
	public int readUnsignedByte() throws IOException
	{
		return file.readUnsignedByte();
	}

	@Override
	public void readFully(byte[] buffer) throws IOException
	{
		file.readFully(buffer);
	}

	/*
	 * CompactReader style access
	 */

	// an InputStream wrapper at the current position of the file
	private InputStream currentInputStream = new InputStream() {

		@Override
		public int read() throws IOException
		{
			return file.read();
		}
	};

	// a CompactReader reading from the current position of the file via the
	// InputStream wrapper
	private CompactReaderInputStream currentCompactReader = new CompactReaderInputStream(
			currentInputStream);

	@Override
	public int readVariableLengthUnsignedInteger() throws IOException
	{
		return currentCompactReader.readVariableLengthUnsignedInteger();
	}
}
