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
