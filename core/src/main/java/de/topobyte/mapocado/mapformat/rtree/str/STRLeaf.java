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
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;
import de.topobyte.mapocado.mapformat.rtree.ITreeLeaf;

public class STRLeaf<T> extends AbstractSTRTreeElement implements ITreeLeaf<T>
{
	private List<STREntry<T>> entries;

	public STRLeaf(List<STRConstructionElement<T>> constructionElements)
	{
		super(createBox(constructionElements));
		entries = new ArrayList<>();
		for (STRConstructionElement<T> element : constructionElements) {
			STREntry<T> entry = new STREntry<>(element.getBoundingBox(),
					element.getObject());
			entries.add(entry);
		}
	}

	private static <T> BoundingBox createBox(
			List<STRConstructionElement<T>> constructionElements)
	{
		if (constructionElements.size() == 0) {
			return new BoundingBox(0, 0, 0, 0);
		}
		BoundingBox box = constructionElements.get(0).getBoundingBox();
		for (int i = 1; i < constructionElements.size(); i++) {
			STRConstructionElement<T> element = constructionElements.get(i);
			box = box.include(element.getBoundingBox());
		}
		return box;
	}

	@Override
	public boolean isInner()
	{
		return false;
	}

	@Override
	public boolean isLeaf()
	{
		return true;
	}

	@Override
	public List<? extends ITreeEntry<T>> getChildren()
	{
		return entries;
	}

}
