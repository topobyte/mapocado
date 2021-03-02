package de.topobyte.mapocado.mapformat.util;

import java.io.IOException;
import java.io.InputStream;

public class ReaderHelper
{
	public static int readUI32(InputStream is) throws IOException
	{
		int b1 = is.read();
		int b2 = is.read();
		int b3 = is.read();
		int b4 = is.read();
		return b4 << 24 | b3 << 16 | b2 << 8 | b1;
	}

	public static int readSI16(InputStream is) throws IOException
	{
		int result = readUI16(is);
		if ((result & 0x00008000) != 0)
			result |= 0xFFFF0000;
		return result;
	}

	public static int readUI16(InputStream is) throws IOException
	{
		int b1 = is.read();
		int b2 = is.read();
		return b2 << 8 | b1;
	}

	public static int readVarUINT(InputStream is) throws IOException
	{
		char t = (char) is.read();
		// System.out.println(String.format("0x%x", (int)t));

		int bytes = 0;
		if ((t & 0x80) == 0) {
			bytes = 1;
		} else if ((t & 0x40) == 0) {
			bytes = 2;
		} else if ((t & 0x20) == 0) {
			bytes = 3;
		} else if ((t & 0x10) == 0) {
			bytes = 4;
		} else {
			bytes = 5;
		}
		// System.out.println("bytes " + bytes);
		// System.out.println("bytes: " + bytes);
		// printf("r bytes %d\n", bytes);

		int value = 0;

		switch (bytes) {
		case (1): {
			// printf("t: %X\n", t);
			value = t;
			break;
		}
		case (2): {
			char u = (char) is.read();
			// printf("t: %X\n", t);
			// printf("u: %X\n", u);
			value = (((t & 0x7F) << 8) | u) + 0x80;
			break;
		}
		case (3): {
			int s = readUI16(is);
			value = (((t & 0x3F) << 16) | s) + 0x4080;
			break;
		}
		case (4): {
			char u = (char) is.read();
			short s = (short) ReaderHelper.readUI16(is);
			value = (((t & 0x1F) << 24) | (u << 16) | s) + 0x204080;
			break;
		}
		case (5): {
			int s = ReaderHelper.readUI32(is);
			value = s;
			break;
		}
		}

		return value;
	}

	public static double readDouble(InputStream is) throws IOException
	{
		long bits = (((long) readUI32(is)) << 32) | readUI32(is);
		double d = Double.longBitsToDouble(bits);
		return d;
	}
}
