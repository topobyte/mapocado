package de.topobyte.mapocado.mapformat.rtree.disk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.util.CompactWriter;

public class CompressedBoundingBox
{
	/*
	 * The compressed bounding box stores a bounding box 'box' relative to a
	 * bounding box 'parent' that contains 'box'. This knowledge gets exploited
	 * in storing 'box' with reduced data: we encode the pair (xMin, yMin) as a
	 * non-negative integer relative to 'parent's' (xMin, yMin) and then we
	 * store (xMax, yMax) as a non-negative integer relative to (xMin, yMin) of
	 * 'box'.
	 */

	private int dx1; /* stores (box.minX - parent.minX) */
	private int dx2; /* stores (box.minY - parent.minY) */
	private int dy1; /* stores (box.maxX - box.minX) */
	private int dy2; /* stores (box.maxY - box.minY) */

	private int spanX;
	private int spanY;

	public CompressedBoundingBox(BoundingBox parent, BoundingBox box)
			throws IOException
	{
		int baseX = parent.getMinX();
		int baseY = parent.getMinY();

		spanX = parent.getMaxX() - parent.getMinX();
		spanY = parent.getMaxY() - parent.getMinY();

		dx1 = box.getMinX() - baseX;
		dx2 = box.getMaxX() - box.getMinX();
		dy1 = box.getMinY() - baseY;
		dy2 = box.getMaxY() - box.getMinY();
	}

	public byte[] getBytes() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CompactWriter writer = new CompactWriter(baos);

		writeValue(writer, dx1, spanX);
		writeValue(writer, dx2, spanX);
		writeValue(writer, dy1, spanY);
		writeValue(writer, dy2, spanY);

		return baos.toByteArray();
	}

	private int writeValue(CompactWriter writer, int value, int span)
			throws IOException
	{
		if (span < 0xff) {
			writer.writeByte(value);
			return 1;
		} else {
			return writer.writeVariableLengthUnsignedInteger(value);
		}
	}

	public BoundingBox getBoundingBox(BoundingBox parent)
	{
		int baseX = parent.getMinX();
		int baseY = parent.getMinY();

		int x1 = baseX + dx1;
		int x2 = x1 + dx2;
		int y1 = baseY + dy1;
		int y2 = y1 + dy2;

		return new BoundingBox(x1, x2, y1, y2);
	}

	public static Entry decompress(Entry pointingEntry, int dx1, int dx2,
			int dy1, int dy2)
	{
		int baseX = pointingEntry.x1;
		int baseY = pointingEntry.y1;

		int x1 = baseX + dx1;
		int x2 = x1 + dx2;
		int y1 = baseY + dy1;
		int y2 = y1 + dy2;

		return new Entry(x1, x2, y1, y2, 0);
	}

	public static boolean isValidReplacementFor(BoundingBox one,
			BoundingBox other)
	{
		return one.getMinX() <= other.getMinX()
				&& one.getMinY() <= other.getMinY()
				&& one.getMaxX() >= other.getMaxX()
				&& one.getMaxY() >= other.getMaxY();
	}

}
