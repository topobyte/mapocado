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
