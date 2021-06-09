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
import java.nio.charset.Charset;

import de.topobyte.mapocado.mapformat.util.ioparam.IntResult;
import de.topobyte.mapocado.mapformat.util.ioparam.StringResult;

public class CompactReaderBuffer
{

	public static int readByte(byte[] buffer, int offset, IntResult output)
	{
		int result = buffer[offset++] & 0xFF;
		output.value = result;
		return offset;
	}

	public static int readVariableLengthUnsignedInteger(byte[] buffer,
			int offset, IntResult output) throws IOException
	{
		int result = 0;
		int shift = 0;
		while (true) {
			int b = buffer[offset++];
			result |= (b & 0x7F) << shift;
			shift += 7;
			if ((b & 0x80) == 0) {
				break;
			}
		}
		output.value = result;
		return offset;
	}

	public static int readVariableLengthSignedInteger(byte[] buffer, int offset,
			IntResult output) throws IOException
	{
		int result = 0;
		int shift = 0;
		while (true) {
			int b = buffer[offset++];
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
		output.value = result;
		return offset;
	}

	private static Charset charset = Charset.forName("UTF-8");

	public static int readString(byte[] buffer, int offset, IntResult temp,
			StringResult output) throws IOException
	{
		offset = readVariableLengthUnsignedInteger(buffer, offset, temp);
		int len = temp.value;
		byte[] sbuffer = new byte[len];
		System.arraycopy(buffer, offset, sbuffer, 0, len);
		// output.value = new String(sbuffer, charset);
		output.value = new String(sbuffer);
		return offset + len;
	}

	public static int readString(byte[] buffer, int offset, int len,
			StringResult output) throws IOException
	{
		byte[] sbuffer = new byte[len];
		System.arraycopy(buffer, offset, sbuffer, 0, len);
		// output.value = new String(sbuffer, charset);
		output.value = new String(sbuffer);
		return offset + len;
	}

}
