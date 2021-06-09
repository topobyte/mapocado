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

package de.topobyte.mapocado.mapformat.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.topobyte.mapocado.mapformat.rtree.disk.Entry;

public interface Byteable
{

	public void clear();

	public void read(InputStream is, Entry entry, Object metadata)
			throws IOException;

	public int read(byte[] buffer, Entry entry, Object metadata)
			throws IOException;

	public void write(OutputStream os, Entry entry, Object metadata)
			throws IOException;

	public Byteable readObject(byte[] buffer, Entry entry, Object metadata)
			throws IOException;
}
