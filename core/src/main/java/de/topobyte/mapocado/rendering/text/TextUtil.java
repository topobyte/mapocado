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

package de.topobyte.mapocado.rendering.text;

import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.util.ioparam.BoolResult;

public class TextUtil
{

	/**
	 * Create an array of boxes along the specified line. The creation of boxes
	 * will start at {@code hOffset} along the string, and generate boxes for
	 * the length specified with the {@code textLength} parameter. The method
	 * will return an array of int representing the boxes created. Let
	 * {@code boxes} be that array, then {@code boxes[i]} will contain the i'th
	 * box created.
	 * 
	 * @param string
	 *            the string to create boxes along
	 * @param hOffset
	 *            the amount to skip on the way before starting box creation.
	 * @param textLength
	 *            the length for which to create boxes.
	 * @param textHeight
	 *            the height of the boxes.
	 * @param isReverseX
	 *            an out parameter telling whether the area the boxes have been
	 *            created for is reverse in respect to the x coordinate, i.e.
	 *            the first coordinate is on the right of the last coordinate.
	 * @return an array of int describing boxes.
	 */
	public static int[][] createTextBoxes(Linestring string, double hOffset,
			double textLength, double textHeight, BoolResult isReverseX)
	{
		// general stuff
		int num = string.getNumberOfCoordinates();
		double h = textHeight / 2;

		// place to store previous coordinate
		int lastX = string.x[0];
		int lastY = string.y[0];

		int i = 1; // loop variable for all loops
		double stillToSkip = hOffset; // remaining skip amount

		// ********************************************************************
		// first skip through segments until hOffset is 0
		// ********************************************************************
		if (hOffset > 0) {
			for (; i < num; i++) {
				int thisX = string.x[i];
				int thisY = string.y[i];
				int dx = thisX - lastX;
				int dy = thisY - lastY;
				double lengthSegment = Math.sqrt(dx * dx + dy * dy);
				if (lengthSegment < stillToSkip) {
					// easy case, still enough to skip, continue loop
					stillToSkip -= lengthSegment;
					lastX = thisX;
					lastY = thisY;
					continue;
				}
				// we have to stop somewhere in between
				double skip = stillToSkip / lengthSegment;
				lastX = (int) Math.round(lastX + skip * dx);
				lastY = (int) Math.round(lastY + skip * dy);
				break;
			}
		}

		// keep reference to first x coordinate
		int firstX = lastX;

		// variables for further iteration
		int[][] boxes = new int[num - i][];
		int k = 0;
		double stillToTake = textLength;

		// ********************************************************************
		// then iterate through the segments
		// ********************************************************************
		for (; i < num; i++) {
			int thisX = string.x[i];
			int thisY = string.y[i];

			int dx = thisX - lastX;
			int dy = thisY - lastY;
			double lengthSegment = Math.sqrt(dx * dx + dy * dy);

			// test whether we need only part of the current segment
			boolean end = false;
			if (lengthSegment > stillToTake) {
				end = true;
				// do not take the whole segment
				double take = stillToTake / lengthSegment;
				// redefine current point
				thisX = (int) Math.round(lastX + take * dx);
				thisY = (int) Math.round(lastY + take * dy);
				// redefine dx, dy, lengthSegment accordingly
				dx = thisX - lastX;
				dy = thisY - lastY;
				lengthSegment = stillToTake;
			}

			// calculate rectangle
			double lambda = h / lengthSegment;
			double ox = -dy * lambda;
			double oy = dx * lambda;

			int r1x = (int) Math.round(lastX + ox);
			int r1y = (int) Math.round(lastY + oy);
			int r2x = (int) Math.round(lastX - ox);
			int r2y = (int) Math.round(lastY - oy);
			int r3x = (int) Math.round(thisX - ox);
			int r3y = (int) Math.round(thisY - oy);
			int r4x = (int) Math.round(thisX + ox);
			int r4y = (int) Math.round(thisY + oy);

			lastX = thisX;
			lastY = thisY;
			stillToTake -= lengthSegment;

			int[] box = new int[8];
			box[0] = r1x;
			box[1] = r1y;
			box[2] = r2x;
			box[3] = r2y;
			box[4] = r3x;
			box[5] = r3y;
			box[6] = r4x;
			box[7] = r4y;
			boxes[k++] = box;

			if (end || stillToTake <= 0) {
				break;
			}
		}

		// we may have used less segments in the end, so cut the array if
		// necessary
		if (k < boxes.length) {
			int[][] shorter = new int[k][];
			System.arraycopy(boxes, 0, shorter, 0, k);
			boxes = shorter;
		}

		// check whether this is reverse
		isReverseX.value = lastX < firstX;

		return boxes;
	}

}
