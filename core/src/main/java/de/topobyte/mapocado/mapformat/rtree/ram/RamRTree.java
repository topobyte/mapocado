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
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;
import de.topobyte.mapocado.mapformat.rtree.ITreeLeaf;
import de.topobyte.melon.casting.CastUtil;

/**
 * A RamRTree is a rtree implementation that is completely stored in RAM. Its
 * main purpose is to be created from an instance of GenericRTree and then be
 * used to populate a DiskTree instance.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 * @param <T>
 *            the type of elements.
 */
public class RamRTree<T> implements IRTree<T>, Serializable
{

	private static final long serialVersionUID = -7327622728126684984L;

	private final ITreeElement root;
	private final int height;

	public RamRTree(ITreeElement root, int height)
	{
		this.root = root;
		this.height = height;
	}

	@Override
	public ITreeElement getRoot()
	{
		return root;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	public List<T> intersectionQuery(BoundingBox queryBox)
	{
		List<T> results = query(queryBox);
		return results;
	}

	private List<T> query(BoundingBox queryBox)
	{
		List<T> results = new ArrayList<>();
		if (!root.intersects(queryBox)) {
			return results;
		}
		queryElement(results, root, queryBox);
		return results;
	}

	private void queryElement(List<T> results, ITreeElement node,
			BoundingBox queryBox)
	{
		if (node instanceof TreeNode) {
			query(results, (TreeNode) node, queryBox);
		} else if (node instanceof TreeLeaf) {
			queryLeaf(results, node, queryBox);
		}
	}

	private void query(List<T> results, TreeNode node, BoundingBox queryBox)
	{
		List<ITreeElement> children = node.getChildren();
		for (ITreeElement child : children) {
			if (!child.intersects(queryBox)) {
				continue;
			}
			if (child.isInner()) {
				query(results, (TreeNode) child, queryBox);
			} else if (child.isLeaf()) {
				queryLeaf(results, child, queryBox);
			}
		}
	}

	private void queryLeaf(List<T> results, ITreeElement child,
			BoundingBox queryBox)
	{
		ITreeLeaf<T> leaf = CastUtil.cast(child);
		for (ITreeEntry<T> entry : leaf.getChildren()) {
			if (entry.intersects(queryBox)) {
				T thing = entry.getThing();
				results.add(thing);
			}
		}
	}

}
