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
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeNode;

public class TreeNode extends AbstractTreeElement
		implements ITreeNode, Serializable
{

	private static final long serialVersionUID = 7746644577163505161L;

	private List<ITreeElement> children = new ArrayList<>();

	public TreeNode()
	{
		// serializable constructor
		super();
	}

	public TreeNode(BoundingBox box)
	{
		super(box);
	}

	@Override
	public List<ITreeElement> getChildren()
	{
		return children;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	public boolean isInner()
	{
		return true;
	}
}
