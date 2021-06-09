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

package de.topobyte.mapocado.mapformat.rtree.disk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.mapformat.model.Byteable;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.ITreeEntry;
import de.topobyte.mapocado.mapformat.rtree.ITreeLeaf;
import de.topobyte.mapocado.mapformat.rtree.ITreeNode;
import de.topobyte.mapocado.mapformat.util.CompactWriter;
import de.topobyte.melon.casting.CastUtil;

public abstract class AbstractDiskTreeCreator<T extends Byteable>
{
	final static Logger logger = LoggerFactory
			.getLogger(AbstractDiskTreeCreator.class);

	final RandomAccessFile file;
	final int offset;

	int position = 0;
	Object metadata;

	int nodeCount = 0;
	int leafCount = 0;
	int entryCount = 0;

	public AbstractDiskTreeCreator(RandomAccessFile file, int offset,
			Object metadata) throws IOException
	{
		this.file = file;
		this.offset = offset;
		this.metadata = metadata;

		logger.debug("seeking to: " + offset);
		file.seek(offset);
	}

	private OutputStream currentOutputStream = new OutputStream() {

		@Override
		public void write(int b) throws IOException
		{
			file.write(b);
		}
	};

	CompactWriter currentCompactWriter = new CompactWriter(currentOutputStream);

	void startTraversal(ITreeElement element, int height) throws IOException
	{
		// data
		file.write(height);
		position = 1;
		Entry rootEntry = new Entry(element.getBoundingBox(), 21);
		position += writeEntry(rootEntry, 1);
		traverse(element, 1, null);
	}

	private int traverse(ITreeElement element, int entryAddress,
			ITreeElement parent) throws IOException
	{
		if (element instanceof ITreeNode) {
			return traverse((ITreeNode) element, parent);
		} else if (element instanceof ITreeLeaf) {
			ITreeLeaf<T> leaf = CastUtil.cast(element);
			return traverse(leaf, entryAddress, parent);
		}
		return 0;
	}

	/**
	 * Inner node writing
	 */
	// Note: we're keeping the unused parameter 'parent' because we could one
	// day use it to achieve compression of the bounding box stored in the
	// entries by storing deltas to the parent bounding box with variable length
	// integers. At the moment we're only doing this at the deepest level (we
	// trade a little space in the inner node for read performance)
	private int traverse(ITreeNode node, ITreeElement parent) throws IOException
	{
		nodeCount++;
		// logger.debug(String.format("node at position %X", position));
		int nChildren = node.getChildren().size();
		// 1 byte for # children
		// + (# children * entry size)
		int size = 1 + nChildren * Constants.SIZE_ENTRY;

		int myPosition = position;
		file.seek(offset + position);
		file.write(nChildren);

		position = position + size;

		List<Entry> entries = new ArrayList<>();
		for (int i = 0; i < node.getChildren().size(); i++) {
			ITreeElement child = node.getChildren().get(i);
			int childAddress = traverse(child,
					myPosition + 1 + i * Constants.SIZE_ENTRY, node);
			Entry entry = new Entry(child.getBoundingBox(), childAddress);
			entries.add(entry);
		}
		for (int i = 0; i < entries.size(); i++) {
			Entry entry = entries.get(i);
			writeEntry(entry, myPosition + 1 + i * Constants.SIZE_ENTRY);
		}
		return myPosition;
	}

	public abstract int traverse(ITreeLeaf<T> leaf, int entryAddress,
			ITreeElement parent) throws IOException;

	private int writeEntry(Entry entry, int position) throws IOException
	{
		file.seek(offset + position);
		file.writeInt(entry.x1);
		file.writeInt(entry.x2);
		file.writeInt(entry.y1);
		file.writeInt(entry.y2);
		// logger.debug(String.format("writing entry: %d, %d, %d, %d",
		// entry.x1, entry.x2, entry.y1, entry.y2));
		file.writeInt(entry.address);
		return 20;
	}

	int writeCompressedEntry(CompressedBoundingBox box, Entry entry)
			throws IOException
	{
		byte[] bytes = box.getBytes();
		file.write(bytes);
		int len = currentCompactWriter
				.writeVariableLengthUnsignedInteger(entry.size);
		return bytes.length + len;
	}

	int traverse(ITreeEntry<T> treeEntry, ITreeElement parent,
			Entry pointingEntry) throws IOException
	{
		entryCount++;
		// logger.debug(String.format("entry at position %X", position));
		int myPosition = position;
		file.seek(offset + position);
		byte[] bytes = getBytes(pointingEntry, treeEntry);
		file.write(bytes);
		position = position + bytes.length;
		return myPosition;
	}

	int getSize(Entry pointingEntry, ITreeEntry<T> entry) throws IOException
	{
		byte[] bytes = getBytes(pointingEntry, entry);
		return bytes.length;
	}

	private byte[] getBytes(Entry pointingEntry, ITreeEntry<T> entry)
			throws IOException
	{
		Byteable thing = entry.getThing();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		thing.write(baos, pointingEntry, metadata);
		baos.flush();
		return baos.toByteArray();
	}

}
