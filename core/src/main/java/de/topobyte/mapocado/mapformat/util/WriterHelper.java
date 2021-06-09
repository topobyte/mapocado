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
import java.io.OutputStream;

public class WriterHelper
{
	public static void writeUI32(OutputStream os, int x) throws IOException
	{
		os.write(x);
		os.write(x >> 8);
		os.write(x >> 16);
		os.write(x >> 24);
	}

	public static void writeSI16(OutputStream os, int x) throws IOException
	{
		// if ((result & 0x00008000) != 0)
		// result |= 0xFFFF0000;
		writeUI16(os, x);
	}

	public static void writeUI16(OutputStream os, int x) throws IOException
	{
		os.write(x);
		os.write(x >> 8);
	}

	public static void writeVarUINT(OutputStream os, int x) throws IOException
	{
		int ix = x;// (x >= 0) ? x : ~x;
		int bytes = 0;
		if ((ix & 0xFFFFFF80) == 0) {
			bytes = 1;
		} else if (((ix - 0x80) & 0xFFFFC000) == 0) {
			bytes = 2;
		} else if (((ix - 0x4080) & 0xFFE00000) == 0) {
			bytes = 3;
		} else if (((ix - 0x204080) & 0xF0000000) == 0) {
			bytes = 4;
		} else {
			bytes = 5;
		}

		switch (bytes) {
		case (1): {
			os.write(ix);
			break;
		}
		case (2): {
			x -= 0x80;
			int t = 0x80 | (x >> 8);
			int s = x & 0x000000FF;
			os.write(t);
			os.write(s);
			break;
		}
		case (3): {
			x -= 0x4080;
			int t = 0xC0 | (x >> 16);
			int s = x & 0x0000FFFF;
			os.write(t);
			os.write(s);
			os.write(s >> 8);
			break;
		}
		case (4): {
			x -= 0x204080;
			int t = 0xE0 | (x >> 24);
			int u = 0x000000FF & (x >> 16);
			int s = x & 0x0000FFFF;
			os.write(t);
			os.write(u);
			os.write(s);
			os.write(s >> 8);
			break;
		}
		case (5): {
			char t = 0xF0;
			os.write(t);
			os.write(x);
			os.write(x >> 8);
			os.write(x >> 16);
			os.write(x >> 24);
			break;
		}
		}
	}

	public static void writeDouble(OutputStream os, double x) throws IOException
	{
		long bits = Double.doubleToLongBits(x);
		writeUI32(os, (int) (bits >> 32));
		writeUI32(os, (int) (bits));
	}
}
