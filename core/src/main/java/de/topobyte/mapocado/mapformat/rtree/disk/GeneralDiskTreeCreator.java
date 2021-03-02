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

public class GeneralDiskTreeCreator<T extends Byteable>
		extends AbstractDiskTreeCreator<T>
{

	public static <T extends Byteable> int create(RandomAccessFile file,
			int offset, IRTree<T> ramTree, Object metadata,
			ValueInsertionCallback<T> valueInsertionCallback) throws IOException
	{
		AbstractDiskTreeCreator<T> creator = new GeneralDiskTreeCreator<>(file,
				offset, metadata, valueInsertionCallback);

		ITreeElement root = ramTree.getRoot();
		int height = ramTree.getHeight();
		creator.startTraversal(root, height);

		logger.debug(String.format("nodes: %d, leafs: %d, entries: %d",
				creator.nodeCount, creator.leafCount, creator.entryCount));

		return creator.position;
	}

	private ValueInsertionCallback<T> valueInsertionCallback;

	public GeneralDiskTreeCreator(RandomAccessFile file, int offset,
			Object metadata, ValueInsertionCallback<T> valueInsertionCallback)
			throws IOException
	{
		super(file, offset, metadata);
		this.valueInsertionCallback = valueInsertionCallback;
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
		for (int i = 0; i < leaf.getChildren().size(); i++) {
			ITreeEntry<T> child = leaf.getChildren().get(i);
			BoundingBox box = child.getBoundingBox();
			CompressedBoundingBox compressedBox = new CompressedBoundingBox(
					leafBox, box);
			BoundingBox storedBox = compressedBox.getBoundingBox(leafBox);
			Entry entry = new Entry(storedBox, 0);
			entry.size = getSize(entry, child);

			entries.add(entry);
			lengthEntryTable += writeCompressedEntry(compressedBox, entry);

			// DEBUG: validate compressed entries
			if (!CompressedBoundingBox.isValidReplacementFor(storedBox, box)) {
				logger.error("fail: " + box + " -> " + storedBox);
			}
		}
		size += lengthEntryTable;

		position = position + size;

		for (int i = 0; i < leaf.getChildren().size(); i++) {
			Entry entry = entries.get(i);
			ITreeEntry<T> child = leaf.getChildren().get(i);
			traverse(child, leaf, entry);
			if (valueInsertionCallback != null) {
				valueInsertionCallback.valueInserted(entryAddress, i,
						child.getThing());
			}
		}
		return myPosition;
	}
}
