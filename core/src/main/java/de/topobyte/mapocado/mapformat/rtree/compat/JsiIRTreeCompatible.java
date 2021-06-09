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

package de.topobyte.mapocado.mapformat.rtree.compat;

import java.io.IOException;

import com.infomatiq.jsi.Rectangle;

import de.topobyte.jsi.GenericRTree;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.ram.Converter;

public class JsiIRTreeCompatible<T> extends GenericRTree<T>
		implements IRTreeCompatible<T>
{

	public JsiIRTreeCompatible(int min, int max)
	{
		super(min, max);
	}

	@Override
	public IRTree<T> createIRTree() throws IOException
	{
		try {
			return Converter.create(this);
		} catch (SecurityException e) {
			throw new IOException(e);
		} catch (IllegalArgumentException e) {
			throw new IOException(e);
		} catch (NoSuchFieldException e) {
			throw new IOException(e);
		} catch (IllegalAccessException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void add(BoundingBox rect, T object)
	{
		Rectangle r = new Rectangle(rect.getMinX(), rect.getMinY(),
				rect.getMaxX(), rect.getMaxY());
		add(r, object);
	}

}
