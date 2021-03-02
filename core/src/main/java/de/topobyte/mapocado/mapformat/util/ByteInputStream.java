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
