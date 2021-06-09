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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;
import de.topobyte.mapocado.mapformat.rtree.ITreeLeaf;

public class TreeLeaf<T> extends AbstractTreeElement
		implements ITreeLeaf<T>, Serializable
{

	private static final long serialVersionUID = -9122375943390815134L;

	private List<TreeEntry<T>> children = new ArrayList<>();

	public TreeLeaf()
	{
		// serializable constructor
		super();
	}

	public TreeLeaf(BoundingBox box)
	{
		super(box);
	}

	@Override
	public boolean isLeaf()
	{
		return true;
	}

	@Override
	public boolean isInner()
	{
		return false;
	}

	@Override
	public List<? extends ITreeEntry<T>> getChildren()
	{
		return children;
	}

}
