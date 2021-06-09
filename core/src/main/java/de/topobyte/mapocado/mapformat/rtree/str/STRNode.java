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

package de.topobyte.mapocado.mapformat.rtree.str;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeNode;

public class STRNode extends AbstractSTRTreeElement implements ITreeNode
{

	List<ITreeElement> childs = new ArrayList<>();

	public STRNode(List<ITreeElement> children)
	{
		super(createBox(children));
		childs.addAll(children);
	}

	private static <T> BoundingBox createBox(List<ITreeElement> children)
	{
		BoundingBox box = children.get(0).getBoundingBox();
		for (int i = 1; i < children.size(); i++) {
			ITreeElement element = children.get(i);
			box = box.include(element.getBoundingBox());
		}
		return box;
	}

	@Override
	public boolean isInner()
	{
		return true;
	}

	@Override
	public boolean isLeaf()
	{
		return false;
	}

	@Override
	public List<ITreeElement> getChildren()
	{
		return childs;
	}
}
