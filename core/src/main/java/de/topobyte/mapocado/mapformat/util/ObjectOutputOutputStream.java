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
