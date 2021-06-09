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

import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;
import de.topobyte.mapocado.mapformat.util.CompactReader;
import de.topobyte.mapocado.mapformat.util.CompactReaderBuffer;
import de.topobyte.mapocado.mapformat.util.CompactReaderInputStream;
import de.topobyte.mapocado.mapformat.util.CompactWriter;
import de.topobyte.mapocado.mapformat.util.ioparam.IntResult;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class Way extends Entity implements Byteable, Closeable
{

	private static final long serialVersionUID = -2150537781221624781L;

	private Linestring string;

	public Way()
	{
		// default constructor needed for externalizability
	}

	public Way(Map<Integer, String> tags, Linestring string)
	{
		super(tags);
		this.string = string;
	}

	public Linestring getString()
	{
		return string;
	}

	@Override
	public boolean isClosed()
	{
		return string.isClosed();
	}

	@Override
	public String toString()
	{
		return string.toString();
	}

	@Override
	public void write(OutputStream os, Entry entry, Object metadata)
			throws IOException
	{
		super.write(os, entry, metadata);

		CompactWriter writer = new CompactWriter(os);

		// number of points as variable unsigned int
		int nPoints = string.getNumberOfCoordinates();
		writer.writeVariableLengthUnsignedInteger(nPoints);

		int lastX = entry.getX1();
		int lastY = entry.getY1();

		// subsequent coordinates difference to previous as variable signed ints
		for (int i = 0; i < nPoints; i++) {
			int ix = string.x[i];
			int iy = string.y[i];

			int dx = ix - lastX;
			int dy = iy - lastY;
			if (i == 0) {
				if (dx < 0 || dy < 0) {
					System.out
							.println("Way write(): dx or dy is outside of bbox"
									+ dx + ", " + dy);
				}
				writer.writeVariableLengthUnsignedInteger(dx);
				writer.writeVariableLengthUnsignedInteger(dy);
			} else {
				writer.writeVariableLengthSignedInteger(dx);
				writer.writeVariableLengthSignedInteger(dy);
			}

			lastX = ix;
			lastY = iy;
		}

	}

	@Override
	public void read(InputStream is, Entry entry, Object metadata)
			throws IOException
	{
		super.read(is, entry, metadata);
		CompactReader reader = new CompactReaderInputStream(is);
		// number of points, unsigned variable integer
		int nPoints = reader.readVariableLengthUnsignedInteger();
		// array of coordinates
		string = new Linestring(nPoints);
		// first value is unsigned
		int lastX = entry.x1 + reader.readVariableLengthUnsignedInteger();
		int lastY = entry.y1 + reader.readVariableLengthUnsignedInteger();
		string.x[0] = lastX;
		string.y[0] = lastY;
		// subsequent values are signed
		for (int i = 1; i < nPoints; i++) {
			lastX = lastX + reader.readVariableLengthSignedInteger();
			lastY = lastY + reader.readVariableLengthSignedInteger();
			string.x[i] = lastX;
			string.y[i] = lastY;
		}
	}

	@Override
	public Byteable readObject(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		Way way = new Way();
		way.read(buffer, entry, metadata);
		return way;
	}

	// result object used in deserialization
	private IntResult ir = new IntResult();

	@Override
	public int read(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		int offset = super.read(buffer, entry, metadata);

		offset = CompactReaderBuffer.readVariableLengthUnsignedInteger(buffer,
				offset, ir);
		int nPoints = ir.value;
		// array of coordinates
		string = new Linestring(nPoints);
		// first value is unsigned
		offset = CompactReaderBuffer.readVariableLengthUnsignedInteger(buffer,
				offset, ir);
		int lastX = entry.x1 + ir.value;
		offset = CompactReaderBuffer.readVariableLengthUnsignedInteger(buffer,
				offset, ir);
		int lastY = entry.y1 + ir.value;
		string.x[0] = lastX;
		string.y[0] = lastY;
		// subsequent values are signed
		for (int i = 1; i < nPoints; i++) {
			offset = CompactReaderBuffer.readVariableLengthSignedInteger(buffer,
					offset, ir);
			lastX = lastX + ir.value;
			offset = CompactReaderBuffer.readVariableLengthSignedInteger(buffer,
					offset, ir);
			lastY = lastY + ir.value;
			string.x[i] = lastX;
			string.y[i] = lastY;
		}

		return offset;
	}
}
