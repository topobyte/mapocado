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

import de.topobyte.mapocado.mapformat.geom.LinearRing;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.geom.Polygon;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;
import de.topobyte.mapocado.mapformat.util.ByteInputStream;
import de.topobyte.mapocado.mapformat.util.CompactReaderInputStream;
import de.topobyte.mapocado.mapformat.util.CompactWriter;

public class Relation extends Entity implements Byteable, Closeable
{

	private static final long serialVersionUID = 3160762506988829549L;

	private Multipolygon multipolygon;

	public Relation()
	{
		// default constructor needed for externalizability
	}

	public Relation(Map<Integer, String> tags, Multipolygon polygon)
	{
		super(tags);
		this.multipolygon = polygon;
	}

	public Multipolygon getPolygon()
	{
		return multipolygon;
	}

	@Override
	public boolean isClosed()
	{
		return true;
	}

	@Override
	public Byteable readObject(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		Relation relation = new Relation();
		relation.read(buffer, entry, metadata);
		return relation;
	}

	@Override
	public void read(InputStream is, Entry entry, Object metadata)
			throws IOException
	{
		super.read(is, entry, metadata);
		CompactReaderInputStream reader = new CompactReaderInputStream(is);

		int numGeometries = reader.readVariableLengthUnsignedInteger();
		Polygon[] polygons = new Polygon[numGeometries];
		for (int i = 0; i < numGeometries; i++) {
			LinearRing exterior = read(reader, entry);
			int numInterior = reader.readVariableLengthUnsignedInteger();
			LinearRing[] interiors = new LinearRing[numInterior];
			for (int k = 0; k < numInterior; k++) {
				interiors[k] = read(reader, entry);
			}
			polygons[i] = new Polygon(exterior, interiors);
		}
		multipolygon = new Multipolygon(polygons);
	}

	@Override
	public void write(OutputStream os, Entry entry, Object metadata)
			throws IOException
	{
		super.write(os, entry, metadata);
		CompactWriter writer = new CompactWriter(os);

		// System.out.println(multipolygon.getGeometryType());
		int numGeometries = multipolygon.getNumberOfPolygons();
		writer.writeVariableLengthUnsignedInteger(numGeometries);

		// System.out.println("outer: " + numGeometries);
		for (int i = 0; i < numGeometries; i++) {
			Polygon polygon = multipolygon.getPolygon(i);
			LinearRing exterior = polygon.getExteriorRing();
			write(writer, entry, exterior);
			int numInteriorRings = polygon.getNumberOfInteriorRings();
			writer.writeVariableLengthUnsignedInteger(numInteriorRings);
			// System.out.println("inner: " + numInteriorRings);
			for (int k = 0; k < numInteriorRings; k++) {
				LinearRing interior = polygon.getInteriorRing(k);
				write(writer, entry, interior);
			}
		}
	}

	private LinearRing read(CompactReaderInputStream reader, Entry entry)
			throws IOException
	{
		int nPoints = reader.readVariableLengthUnsignedInteger();
		// nPoints plus an extra place for the implicit last coordinate
		LinearRing ring = new LinearRing(nPoints + 1);
		int lastX = entry.x1 + reader.readVariableLengthUnsignedInteger();
		int lastY = entry.y1 + reader.readVariableLengthUnsignedInteger();
		ring.x[0] = ring.x[nPoints] = lastX;
		ring.y[0] = ring.y[nPoints] = lastY;
		for (int i = 1; i < nPoints; i++) {
			lastX = lastX + reader.readVariableLengthSignedInteger();
			lastY = lastY + reader.readVariableLengthSignedInteger();
			ring.x[i] = lastX;
			ring.y[i] = lastY;
		}
		return ring;
	}

	private void write(CompactWriter writer, Entry entry, Linestring string)
			throws IOException
	{
		// number of points as variable unsigned int
		// ignore the last variable, it is implicit
		int nPoints = string.getNumberOfCoordinates() - 1;
		writer.writeVariableLengthUnsignedInteger(nPoints);

		int lastX = entry.getX1();
		int lastY = entry.getY1();

		// first coordinate as a variable length unsigned offset from entry
		// subsequent coordinates difference to previous as variable signed ints
		for (int i = 0; i < nPoints; i++) {
			int x = string.x[i];
			int y = string.y[i];

			int dx = x - lastX;
			int dy = y - lastY;
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
			lastX = x;
			lastY = y;
		}

	}

	@Override
	public int read(byte[] buffer, Entry entry, Object metadata)
			throws IOException
	{
		ByteInputStream is = new ByteInputStream(buffer);
		read(is, entry, metadata);
		return 0;
	}

}
