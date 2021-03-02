package de.topobyte.mapocado.mapformat.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

public class ObjectInputInputStream extends InputStream
{

	private final ObjectInput oi;

	public ObjectInputInputStream(ObjectInput oi)
	{
		this.oi = oi;
	}

	@Override
	public int read() throws IOException
	{
		return oi.read();
	}

	@Override
	public int read(byte[] b) throws IOException
	{
		return oi.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException
	{
		return oi.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException
	{
		return oi.skip(n);
	}

	@Override
	public int available() throws IOException
	{
		return oi.available();
	}

	@Override
	public void close() throws IOException
	{
		oi.close();
	}

	@Override
	public boolean markSupported()
	{
		return false;
	}

}
