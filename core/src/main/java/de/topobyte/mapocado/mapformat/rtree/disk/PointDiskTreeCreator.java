package de.topobyte.mapocado.mapformat.rtree.disk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.model.Byteable;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;
import de.topobyte.mapocado.mapformat.rtree.ITreeLeaf;

public class PointDiskTreeCreator<T extends Byteable>
		extends AbstractDiskTreeCreator<T>
{

	public static <T extends Byteable> int create(RandomAccessFile file,
			int offset, IRTree<T> ramTree, Object metadata) throws IOException
	{
		AbstractDiskTreeCreator<T> creator = new PointDiskTreeCreator<>(file,
				offset, metadata);

		ITreeElement root = ramTree.getRoot();
		int height = ramTree.getHeight();
		creator.startTraversal(root, height);

		logger.debug(String.format("nodes: %d, leafs: %d, entries: %d",
				creator.nodeCount, creator.leafCount, creator.entryCount));

		return creator.position;
	}

	public PointDiskTreeCreator(RandomAccessFile file, int offset,
			Object metadata) throws IOException
	{
		super(file, offset, metadata);
	}

	/**
	 * Leaf writing
	 */
	@Override
	public int traverse(ITreeLeaf<T> leaf, int entryAddress,
			ITreeElement parent) throws IOException
	{
		leafCount++;
		// logger.debug(String.format("leaf at position %X", position));
		int nChildren = leaf.getChildren().size();

		int myPosition = position;
		file.seek(offset + position);
		file.write(nChildren);

		int size = 1; // total size
		int lengthEntryTable = 0; // size of entry table
		List<Entry> entries = new ArrayList<>();
		// leafBox is used to compute relative, compressed values in entries
		BoundingBox leafBox = leaf.getBoundingBox();

		int spanX = leafBox.getMaxX() - leafBox.getMinX();
		int spanY = leafBox.getMaxY() - leafBox.getMinY();

		int lastX = leafBox.getMinX();
		int lastY = leafBox.getMinY();

		for (int i = 0; i < leaf.getChildren().size(); i++) {
			ITreeEntry<T> child = leaf.getChildren().get(i);

			BoundingBox box = child.getBoundingBox();
			int x = box.getMinX();
			int y = box.getMinY();
			int dx = x - lastX;
			int dy = y - lastY;

			Entry entry = new Entry(box, 0);
			entry.size = getSize(entry, child);

			entries.add(entry);
			lengthEntryTable += writeEntry(dx, dy, entry, spanX, spanY);
		}
		size += lengthEntryTable;

		position = position + size;

		for (int i = 0; i < leaf.getChildren().size(); i++) {
			Entry entry = entries.get(i);
			ITreeEntry<T> child = leaf.getChildren().get(i);
			traverse(child, leaf, entry);
		}
		return myPosition;
	}

	private int writeEntry(int dx, int dy, Entry entry, int spanX, int spanY)
			throws IOException
	{
		int len = 0;
		len += writeValue(dx, spanX);
		len += writeValue(dy, spanY);
		len += currentCompactWriter
				.writeVariableLengthUnsignedInteger(entry.size);
		return len;
	}

	private int writeValue(int value, int span) throws IOException
	{
		if (span < 0xff) {
			currentCompactWriter.writeByte(value);
			return 1;
		} else {
			return currentCompactWriter
					.writeVariableLengthUnsignedInteger(value);
		}
	}
}
