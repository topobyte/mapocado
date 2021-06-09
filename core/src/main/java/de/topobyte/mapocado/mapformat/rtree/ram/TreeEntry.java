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

package de.topobyte.mapocado.mapformat.rtree.ram;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;

public class TreeEntry<T> extends AbstractTreeElement implements ITreeEntry<T>
{

	private static final long serialVersionUID = 3508733036787176054L;

	private final T thing;

	public TreeEntry(BoundingBox boundingBox, T thing)
	{
		super(boundingBox);
		this.thing = thing;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	public boolean isInner()
	{
		return false;
	}

	@Override
	public T getThing()
	{
		return thing;
	}

}
