package de.topobyte.mapocado.mapformat.util;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

public class ObjectOutputOutputStream extends OutputStream
{

	private final ObjectOutput oo;

	public ObjectOutputOutputStream(ObjectOutput oo)
	{
		this.oo = oo;
	}

	@Override
	public void write(int b) throws IOException
	{
		oo.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException
	{
		oo.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException
	{
		oo.write(b, off, len);
	}

	@Override
	public void flush() throws IOException
	{
		oo.flush();
	}

	@Override
	public void close() throws IOException
	{
		oo.close();
	}

}
