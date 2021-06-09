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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class CompactReaderInputStream implements CompactReader
{

	private final InputStream is;

	public CompactReaderInputStream(InputStream is)
	{
		this.is = is;
	}

	@Override
	public int readByte() throws IOException
	{
		return is.read();
	}

	@Override
	public int readVariableLengthUnsignedInteger() throws IOException
	{
		int result = 0;
		int shift = 0;
		while (true) {
			int b = readByte();
			result |= (b & 0x7F) << shift;
			shift += 7;
			if ((b & 0x80) == 0) {
				break;
			}
		}
		return result;
	}

	@Override
	public int readVariableLengthSignedInteger() throws IOException
	{
		int result = 0;
		int shift = 0;
		while (true) {
			int b = readByte();
			result |= (b & 0x7F) << shift;
			shift += 7;
			if ((b & 0x80) == 0) {
				break;
			}
		}
		if ((1 << (shift - 1) & result) != 0) {
			// first significant bit is 1
			switch (shift) {
			case 7:
				result |= 0xFFFFFF80;
				break;
			case 14:
				result |= 0xFFFFC000;
				break;
			case 21:
				result |= 0xFFE00000;
				break;
			case 28:
				result |= 0xF0000000;
				break;
			}
		}
		return result;
	}

	@Override
	public int readInt32() throws IOException
	{
		int result = 0;
		result |= readByte();
		result |= readByte() << 8;
		result |= readByte() << 16;
		result |= readByte() << 24;
		return result;
	}

	private static Charset charset = Charset.forName("UTF-8");

	@Override
	public String readString() throws IOException
	{
		int length = readVariableLengthUnsignedInteger();
		byte[] buffer = new byte[length];
		readFully(buffer);
		// return new String(buffer, charset);
		return new String(buffer);
	}

	private void readFully(byte[] buffer) throws IOException
	{
		readFully(buffer, 0, buffer.length);
	}

	private void readFully(byte[] buffer, int off, int len) throws IOException
	{
		while (len > 0) {
			int n = is.read(buffer, off, len);
			if (n < 0) {
				throw new EOFException();
			}
			off += n;
			len -= n;
		}
	}
}
