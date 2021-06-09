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

import java.io.IOException;
import java.io.InputStream;

public class ByteInputStream extends InputStream
{

	private int position = 0;
	private final byte[] buffer;
	private int length = 0;

	public ByteInputStream(byte[] buffer)
	{
		this.buffer = buffer;
		this.length = buffer.length;
	}

	@Override
	public int read() throws IOException
	{
		if (position < length) {
			return buffer[position++];
		}
		return -1;
	}

	public byte[] getBuffer()
	{
		return buffer;
	}
}
