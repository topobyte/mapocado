package de.topobyte.mapocado.mapformat.util;

import java.io.IOException;
import java.io.OutputStream;

public class CompactWriter
{

	private final OutputStream os;

	public CompactWriter(OutputStream os)
	{
		this.os = os;
	}

	public void writeByte(int b) throws IOException
	{
		os.write(b);
	}

	public void writeShort(int n) throws IOException
	{
		writeByte(n);
		writeByte(n >> 8);
	}

	public static int getNumberOfBytesUnsigned(int i)
	{
		if ((i & 0xFFFFFF80) == 0) { // first 25 bits 0
			return 1;
		} else if ((i & 0xFFFFC000) == 0) { // first 18 bits 0
			return 2;
		} else if ((i & 0xFFE00000) == 0) { // first 11 bits 0
			return 3;
		} else if ((i & 0xF0000000) == 0) { // first 4 bits 0
			return 4;
		} else { // otherwise
			return 5;
		}
	}

	public static int getNumberOfBytesSigned(int s)
	{
		if ((s & 0xFFFFFFC0) == 0) { // first 26 bits 0
			return 1;
		} else if ((s & 0xFFFFE000) == 0) { // first 19 bits 0
			return 2;
		} else if ((s & 0xFFF00000) == 0) { // first 12 bits 0
			return 3;
		} else if ((s & 0xF8000000) == 0) { // first 5 bits 0
			return 4;
		} else if ((s & 0x80000000) == 0) { // first bit is 0
			return 5;
		} else if ((s | 0x0000003F) == 0xFFFFFFFF) { // first 26 bits 1
			return 1;
		} else if ((s | 0x00001FFF) == 0xFFFFFFFF) { // first 19 bits 1
			return 2;
		} else if ((s | 0x000FFFFF) == 0xFFFFFFFF) { // first 12 bits 1
			return 3;
		} else if ((s | 0x07FFFFFF) == 0xFFFFFFFF) { // first 5 bits 1
			return 4;
		} else { // first bit is 1
			return 5;
		}
	}

	public int writeVariableLengthUnsignedInteger(int i) throws IOException
	{
		if ((i & 0xFFFFFF80) == 0) { // first 25 bits 0
			writeByte(i);
			return 1;
		} else if ((i & 0xFFFFC000) == 0) { // first 18 bits 0
			writeByte(i | 0x80);
			writeByte(i >> 7);
			return 2;
		} else if ((i & 0xFFE00000) == 0) { // first 11 bits 0
			writeByte(i | 0x80);
			writeByte((i >> 7) | 0x80);
			writeByte(i >> 14);
			return 3;
		} else if ((i & 0xF0000000) == 0) { // first 4 bits 0
			writeByte(i | 0x80);
			writeByte((i >> 7) | 0x80);
			writeByte((i >> 14) | 0x80);
			writeByte(i >> 21);
			return 4;
		} else { // otherwise
			writeByte(i | 0x80);
			writeByte((i >> 7) | 0x80);
			writeByte((i >> 14) | 0x80);
			writeByte((i >> 21) | 0x80);
			writeByte(i >> 28);
			return 5;
		}
	}

	public int writeVariableLengthSignedInteger(int s) throws IOException
	{
		if ((s & 0xFFFFFFC0) == 0) { // first 26 bits 0
			writeByte(s);
			return 1;
		} else if ((s & 0xFFFFE000) == 0) { // first 19 bits 0
			writeByte(s | 0x80);
			writeByte(s >> 7);
			return 2;
		} else if ((s & 0xFFF00000) == 0) { // first 12 bits 0
			writeByte(s | 0x80);
			writeByte((s >> 7) | 0x80);
			writeByte(s >> 14);
			return 3;
		} else if ((s & 0xF8000000) == 0) { // first 5 bits 0
			writeByte(s | 0x80);
			writeByte((s >> 7) | 0x80);
			writeByte((s >> 14) | 0x80);
			writeByte(s >> 21);
			return 4;
		} else if ((s & 0x80000000) == 0) { // first bit is 0
			writeByte(s | 0x80);
			writeByte((s >> 7) | 0x80);
			writeByte((s >> 14) | 0x80);
			writeByte((s >> 21) | 0x80);
			writeByte(s >> 28);
			return 5;
		} else if ((s | 0x0000003F) == 0xFFFFFFFF) { // first 26 bits 1
			writeByte(s & 0x7F);
			return 1;
		} else if ((s | 0x00001FFF) == 0xFFFFFFFF) { // first 19 bits 1
			writeByte(s | 0x80);
			writeByte((s >> 7) & 0x7F);
			return 2;
		} else if ((s | 0x000FFFFF) == 0xFFFFFFFF) { // first 12 bits 1
			writeByte(s | 0x80);
			writeByte((s >> 7) | 0x80);
			writeByte((s >> 14) & 0x7F);
			return 3;
		} else if ((s | 0x07FFFFFF) == 0xFFFFFFFF) { // first 5 bits 1
			writeByte(s | 0x80);
			writeByte((s >> 7) | 0x80);
			writeByte((s >> 14) | 0x80);
			writeByte((s >> 21) & 0x7F);
			return 4;
		} else { // first bit is 1
			writeByte(s | 0x80);
			writeByte((s >> 7) | 0x80);
			writeByte((s >> 14) | 0x80);
			writeByte((s >> 21) | 0x80);
			writeByte((s >> 28) & 0x7F);
			return 5;
		}
	}

	public void writeInt32(int n) throws IOException
	{
		writeByte(n);
		writeByte(n >> 8);
		writeByte(n >> 16);
		writeByte(n >> 24);
	}

	public void writeString(String string) throws IOException
	{
		byte[] bytes = string.getBytes();
		writeVariableLengthUnsignedInteger(bytes.length);
		os.write(bytes);
	}

	public void writeStringNoSize(String string) throws IOException
	{
		byte[] bytes = string.getBytes();
		os.write(bytes);
	}
}
