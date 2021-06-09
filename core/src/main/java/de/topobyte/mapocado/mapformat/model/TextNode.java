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

import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;
import de.topobyte.mapocado.mapformat.util.CompactReaderBuffer;
import de.topobyte.mapocado.mapformat.util.CompactWriter;
import de.topobyte.mapocado.mapformat.util.ioparam.StringResult;

public class TextNode implements Byteable
{

	private Coordinate point;
	private String text;

	public TextNode()
	{
		// constructor for externalizability
	}

	public TextNode(String text, Coordinate point)
	{
		this.text = text;
		this.point = point;
	}

	@Override
	public void clear()
	{
		// nothing to do here
	}

	public Coordinate getPoint()
	{
		return point;
	}

	public String getText()
	{
		return text;
	}

	@Override
	public void write(OutputStream os, Entry entry, Object metadata)
			throws IOException
	{
		CompactWriter writer = new CompactWriter(os);
		writer.writeStringNoSize(text);
	}

	@Override
	public void read(InputStream is, Entry entry, Object metadata)
			throws IOException
	{
		// ignore
	}

	@Override
	public int read(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		int offset = 0;

		int mx = entry.getX1();
		int my = entry.getY1();

		point = new Coordinate(mx, my);

		StringResult sr = new StringResult();
		CompactReaderBuffer.readString(buffer, 0, buffer.length, sr);
		text = sr.value;

		return offset;
	}

	@Override
	public Byteable readObject(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		TextNode h = new TextNode();
		h.read(buffer, entry, metadata);
		return h;
	}

}
