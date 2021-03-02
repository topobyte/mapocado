package de.topobyte.mapocado.mapformat.rtree.ram;

import java.util.List;

import com.infomatiq.jsi.Rectangle;
import com.infomatiq.jsi.rtree.Node;
import com.infomatiq.jsi.rtree.RTree;
import com.slimjars.dist.gnu.trove.map.TIntObjectMap;

import de.topobyte.jsi.GenericRTree;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;
import de.topobyte.mapocado.mapformat.rtree.ITreeLeaf;
import de.topobyte.melon.casting.CastUtil;
import de.topobyte.melon.reflection.Reflection;

/**
 * Methods for construction of RamRTree instances from GenericRTrees.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class Converter
{

	/**
	 * Create a RamRTree from the given GenericRTree.
	 * 
	 * @param <T>
	 *            element type
	 * @param inputTree
	 *            the tree to copy.
	 * @return the newly created tree.
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 */
	public static <T> IRTree<T> create(GenericRTree<T> inputTree)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException
	{
		RTree internalTree = (RTree) Reflection.getObject(inputTree, 0,
				"rtree");
		int rootId = internalTree.getRootNodeId();
		int treeHeight = Reflection.getInt(internalTree, 0, "treeHeight");
		TIntObjectMap<T> idToThing = CastUtil
				.cast(Reflection.getObject(inputTree, 0, "idToThing"));

		Node internalRoot = internalTree.getNode(rootId);

		ITreeElement root = convertNode(internalTree, internalRoot, idToThing);
		RamRTree<T> diskRTree = new RamRTree<>(root, treeHeight);

		return diskRTree;
	}

	private static <T> ITreeElement convertNode(RTree internalTree, Node node,
			TIntObjectMap<T> idToThing)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException
	{
		Rectangle mbb = node.getMbb();
		BoundingBox boundingBox = convertBoundingBox(mbb);

		// System.out.println(node);

		if (node.isLeaf()) {
			ITreeLeaf<T> treeLeaf = new TreeLeaf<>(boundingBox);
			int nc = node.getEntryCount();
			for (int i = 0; i < nc; i++) {
				int elementId = node.getId(i);
				Rectangle elementBox = node.getEntryMbb(i);
				BoundingBox elementBoundingBox = convertBoundingBox(elementBox);
				T thing = idToThing.get(elementId);
				ITreeEntry<T> entry = new TreeEntry<>(elementBoundingBox,
						thing);
				// List<? extends ITreeEntry<T>> children =
				// treeLeaf.getChildren();
				// children.add(entry);

				List<Object> children = CastUtil.cast(treeLeaf.getChildren());
				children.add(entry);
			}
			return treeLeaf;
		}

		TreeNode treeNode = new TreeNode(boundingBox);
		for (int i = 0; i < node.getEntryCount(); i++) {
			int childId = node.getId(i);
			Node child = internalTree.getNode(childId);
			ITreeElement childNode = convertNode(internalTree, child,
					idToThing);
			treeNode.getChildren().add(childNode);
		}
		return treeNode;
	}

	private static BoundingBox convertBoundingBox(Rectangle mbb)
	{
		BoundingBox box = new BoundingBox(mbb.minX, mbb.maxX, mbb.minY,
				mbb.maxY, true);
		return box;
	}

}
