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
import java.util.Map;

import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class Node extends Entity implements Byteable
{

	private static final long serialVersionUID = -2150537781221624782L;

	private Coordinate point;

	// this property is not persitent
	private boolean hasSymbol = false;

	public Node()
	{
		// default constructor needed for externalizability
	}

	public Node(Map<Integer, String> tags, Coordinate point)
	{
		super(tags);
		this.point = point;
	}

	public Coordinate getPoint()
	{
		return point;
	}

	public boolean hasSymbol()
	{
		return hasSymbol;
	}

	public void setHasSymbol(boolean symbol)
	{
		hasSymbol = symbol;
	}

	@Override
	public void write(OutputStream os, Entry entry, Object metadata)
			throws IOException
	{
		super.write(os, entry, metadata);
	}

	@Override
	public void read(InputStream is, Entry entry, Object metadata)
			throws IOException
	{
		super.read(is, entry, metadata);

		int mx = entry.getX1();
		int my = entry.getY1();

		point = new Coordinate(mx, my);
	}

	@Override
	public Byteable readObject(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		Node node = new Node();
		node.read(buffer, entry, metadata);
		return node;
	}

	@Override
	public int read(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		int offset = super.read(buffer, entry, metadata);

		int mx = entry.getX1();
		int my = entry.getY1();

		point = new Coordinate(mx, my);

		return offset;
	}

}
