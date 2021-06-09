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

package de.topobyte.mapocado.rendering.pathtext;

import java.util.ArrayList;
import java.util.List;

import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;

import de.topobyte.jeography.core.Tile;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.rendering.Clipping;

public class LinestringUtil
{
	/**
	 * Calculate the length of a line.
	 * 
	 * @param string
	 *            the string to measure the length of.
	 * @return the length of the string.
	 */
	public static double measurePathLength(Linestring string)
	{
		double total = 0;

		int n = string.getNumberOfCoordinates();

		int lastX = 0, lastY = 0;
		int thisX = 0, thisY = 0;

		lastX = string.x[0];
		lastY = string.y[0];

		for (int i = 1; i < n; i++) {
			thisX = string.x[i];
			thisY = string.y[i];

			int dx = thisX - lastX;
			int dy = thisY - lastY;
			total += Math.sqrt(dx * dx + dy * dy);

			lastX = thisX;
			lastY = thisY;
		}

		return total;
	}

	public static List<Linestring> createChunks(Linestring string,
			double length)
	{
		List<Linestring> chunks = new ArrayList<>();

		TIntArrayList xs = new TIntArrayList();
		TIntArrayList ys = new TIntArrayList();
		double pathLen = 0;

		int lx = string.x[0];
		int ly = string.y[0];
		xs.add(lx);
		ys.add(ly);

		for (int i = 1; i < string.getNumberOfCoordinates(); i++) {
			int cx = string.x[i];
			int cy = string.y[i];
			int dx = cx - lx;
			int dy = cy - ly;
			double segLen = Math.sqrt(dx * dx + dy * dy);

			double left = segLen;
			double tx = lx;
			double ty = ly;
			while (left > 0) {
				if (pathLen + left <= length) {
					// complete segment goes into this chunk
					pathLen += left;
					xs.add(cx);
					ys.add(cy);
					break;
				} else {
					// only a part is needed
					double needed = length - pathLen;
					double useFraction = needed / segLen;
					double sx = tx + dx * useFraction;
					double sy = ty + dy * useFraction;
					int isx = (int) Math.round(sx);
					int isy = (int) Math.round(sy);
					xs.add(isx);
					ys.add(isy);
					tx = sx;
					ty = sy;
					left -= needed;
					// finish path
					Linestring chunk = new Linestring(xs.toArray(),
							ys.toArray());
					chunks.add(chunk);
					// begin a new path
					xs.clear();
					ys.clear();
					pathLen = 0;
					xs.add(isx);
					ys.add(isy);
				}
			}
			lx = cx;
			ly = cy;
		}
		Linestring chunk = new Linestring(xs.toArray(), ys.toArray());
		chunks.add(chunk);

		return chunks;
	}

	public static Clipping createClipping(Tile tile)
	{
		int tx1 = Mercator.getStorageX(tile.getTx(), tile.getZoom());
		int ty1 = Mercator.getStorageY(tile.getTy(), tile.getZoom());
		int tx2 = Mercator.getStorageX(tile.getTx() + 1, tile.getZoom());
		int ty2 = Mercator.getStorageY(tile.getTy() + 1, tile.getZoom());
		return new Clipping(tx1, tx2, ty1, ty2);
	}

	public static Clipping createClipping(Tile tile, float margin)
	{
		int tx1 = Mercator.getStorageX(tile.getTx(), tile.getZoom());
		int ty1 = Mercator.getStorageY(tile.getTy(), tile.getZoom());
		int tx2 = Mercator.getStorageX(tile.getTx() + 1, tile.getZoom());
		int ty2 = Mercator.getStorageY(tile.getTy() + 1, tile.getZoom());
		tx1 = (int) Math.floor(tx1 - margin * (tx2 - tx1));
		tx2 = (int) Math.ceil(tx2 + margin * (tx2 - tx1));
		ty1 = (int) Math.floor(ty1 - margin * (ty2 - ty1));
		ty2 = (int) Math.ceil(ty2 + margin * (ty2 - ty1));
		return new Clipping(tx1, tx2, ty1, ty2);
	}

	public static boolean isRelevant(Clipping clipping, Linestring chunk)
	{
		for (int i = 0; i < chunk.x.length - 1; i++) {
			if (clipping.isNotOutside(chunk.x[i], chunk.y[i], chunk.x[i + 1],
					chunk.y[i + 1])) {
				return true;
			}
		}
		return false;
	}

	public static double curvature(Linestring string)
	{
		double total = 0;

		int n = string.getNumberOfCoordinates();

		// a -- b -- c

		int aX = 0, aY = 0;
		int bX = 0, bY = 0;
		int cX = 0, cY = 0;

		aX = string.x[0];
		aY = string.y[0];
		bX = string.x[1];
		bY = string.y[1];

		double ab, bc, ca;

		int abx = aX - bX;
		int aby = aY - bY;
		ab = Math.sqrt(abx * abx + aby * aby);

		for (int i = 2; i < n; i++) {
			cX = string.x[i];
			cY = string.y[i];

			int bcx = bX - cX;
			int bcy = bY - cY;
			bc = Math.sqrt(bcx * bcx + bcy * bcy);

			int cax = cX - aX;
			int cay = cY - aY;
			ca = Math.sqrt(cax * cax + cay * cay);

			double cos = (ab * ab + bc * bc - ca * ca) / (2 * ab * bc);
			total += (1 + cos) / 2;

			aX = bX;
			aY = bY;
			bX = cX;
			bY = cY;

			ab = bc;
		}

		return total;
	}
}
