package de.topobyte.mapocado.mapformat.rtree.disk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.model.Byteable;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.disk.cache.InnerNodeCache;
import de.topobyte.mapocado.mapformat.rtree.disk.cache.SimpleInnerNodeCache;
import de.topobyte.mapocado.mapformat.rtree.disk.treefile.BufferingTreeAccessFile;
import de.topobyte.mapocado.mapformat.rtree.disk.treefile.ITreeAccessFile;
import de.topobyte.melon.casting.CastUtil;
import de.topobyte.randomaccess.FileAccess;

/**
 * A read only implementation of a disk-based rtree.
 * 
 * This implementation is as following. The tree consists of nodes of which
 * there are inner nodes and leafs.
 * 
 * Inner nodes store entries, which reference other nodes. Such entries have a
 * bounding box and a address pointer to the node within the file.
 * 
 * Leafs store entries as well, but these entries do not reference other nodes
 * but the values that are stored in the tree.
 * 
 * The bounding boxes of entries of inner nodes are stored as uncompressed
 * fixed-size integer values whereas the bounding boxes of entries of leafs are
 * stored as deltas from the leaf's bounding box as variable length integers.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 * @param <T>
 *            the type of elements
 */
public class DiskTree<T extends Byteable>
{
	private final ITreeAccessFile file;
	private final T instance;
	private final Object metadata;

	private final int height;
	private final int leafDepth;

	private static final int POSITION_HEIGHT = 0;
	private static final int POSITION_ROOT_ENTRY = 1;
	private static final int POSITION_ROOT_NODE = 21;

	private final int offset;

	private final boolean isPointTree;

	/**
	 * Create a DiskTree that reads from the given RandomAccesFile instance at
	 * the position denoted by the offset parameter.
	 * 
	 * @param file
	 *            the file to read from.
	 * @param offset
	 *            the offset within the file where the tree's data is stored.
	 * @param instance
	 *            an instance of the type of objects stored for object creation.
	 * @param metadata
	 *            the metadata that was used to store this tree and is passed on
	 *            to element recreation methods.
	 * @param points
	 *            whether this tree is a point tree, i.e. bounding boxes of
	 *            elements are degenerated to points and therefore are stored in
	 *            a more compact manner.
	 * @throws IOException
	 *             if an error occurs while opening or reading from the file.
	 */
	public DiskTree(FileAccess file, int offset, T instance, Object metadata,
			boolean points) throws IOException
	{
		// this.file = new SimpleTreeAccessFile(file);
		this.file = new BufferingTreeAccessFile(file);
		this.offset = offset;
		this.instance = instance;
		this.metadata = metadata;
		this.isPointTree = points;

		file.seek(offset + POSITION_HEIGHT);
		height = file.readUnsignedByte();
		leafDepth = height - 1;
	}

	private Entry rootEntry = null;

	private void ensureRootEntry() throws IOException
	{
		if (rootEntry != null) {
			return;
		}
		file.seek(offset + POSITION_ROOT_ENTRY);
		int x1 = file.readInt();
		int x2 = file.readInt();
		int y1 = file.readInt();
		int y2 = file.readInt();
		int address = file.readInt();
		rootEntry = new Entry(x1, x2, y1, y2, address);
		// System.out.println("root: " + rootEntry);
	}

	/*
	 * *********************************************************************
	 * Intersection query for actual data. Callback variant
	 * *********************************************************************
	 */

	/**
	 * Perform an intersection query on the tree and call a method for each
	 * element within the requested bounding box.
	 */
	public void intersectionQuery(BoundingBox rectRequest,
			ElementCallback<T> handler) throws IOException
	{
		ensureRootEntry();

		// set offset to behind initial entry
		int position = POSITION_ROOT_NODE;
		query(rootEntry, position, rectRequest, 0, handler);
	}

	/**
	 * Internal intersection query method that is aware of the depth of the
	 * query and which also gets a pointing entry as a parameter which may be
	 * used to resolve compression of coordinates.
	 */
	private void query(Entry pointingEntry, int position,
			BoundingBox rectRequest, int depth, ElementCallback<T> handler)
	{
		try {
			// we can only get nodes here, since elements are caught when
			// reaching their containing leafs
			Node node = read(pointingEntry, position, depth);
			// if this is a leaf, process children directly
			if (node.isLeaf) {
				for (Entry entry : node.entries) {
					if (entry.intersects(rectRequest)) {
						readElementIntoInstance(entry, entry.address);
						handler.handle(instance);
					}
				}
				return;
			}
			// not leaf -> inner node
			for (Entry entry : node.entries) {
				if (entry.intersects(rectRequest)) {
					query(entry, entry.address, rectRequest, depth + 1,
							handler);
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Read an element, i.e. an instance of the type of objects stored in this
	 * tree.
	 */
	private void readElementIntoInstance(Entry pointingEntry, int position)
			throws IOException
	{
		file.seek(offset + position);
		int length = pointingEntry.size;
		byte[] buffer = new byte[length];
		file.readFully(buffer);

		instance.clear();
		// ByteInputStream bais = new ByteInputStream(buffer);
		// instance.read(bais, pointingEntry, metadata);
		instance.read(buffer, pointingEntry, metadata);
	}

	/*
	 * *********************************************************************
	 * Intersection query for actual data.
	 * *********************************************************************
	 */

	/**
	 * Perform an intersection query on the tree and return a list of elements
	 * within the requested bounding box.
	 */
	public List<T> intersectionQuery(BoundingBox rectRequest) throws IOException
	{
		List<T> results = new ArrayList<>();

		// System.out.println("intersection query: " + rectRequest);
		ensureRootEntry();

		// set offset to behind initial entry
		int position = POSITION_ROOT_NODE;
		query(rootEntry, position, rectRequest, results, 0);

		return results;
	}

	/**
	 * Internal intersection query method that is aware of the depth of the
	 * query and which also gets a pointing entry as a parameter which may be
	 * used to resolve compression of coordinates.
	 */
	private void query(Entry pointingEntry, int position,
			BoundingBox rectRequest, List<T> results, int depth)
	{
		// System.out.println("query at position: " + position);
		try {
			// we can only get nodes here, since elements are caught when
			// reaching their containing leafs
			Node node = read(pointingEntry, position, depth);
			// if this is a leaf, process children directly
			if (node.isLeaf) {
				for (Entry entry : node.entries) {
					if (entry.intersects(rectRequest)) {
						T thing = readElement(entry, entry.address);
						results.add(thing);
					}
				}
				return;
			}
			// not leaf -> inner node
			for (Entry entry : node.entries) {
				if (entry.intersects(rectRequest)) {
					query(entry, entry.address, rectRequest, results,
							depth + 1);
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Depending on the depth, read either an inner node or a leaf with its
	 * elements.
	 */
	private Node read(Entry pointingEntry, int position, int depth)
			throws IOException
	{
		file.seek(offset + position);
		if (depth != leafDepth) {
			return readInnerNode(pointingEntry, depth);
		} else {
			if (isPointTree) {
				return readLeafNodePoint(pointingEntry, depth);
			} else {
				return readLeafNodeGeneral(pointingEntry, depth);
			}
		}
	}

	// private InnerNodeCache cacheNodes = new DummyInnerNodeCache();
	// private InnerNodeCache cacheNodes = new NaiveInnerNodeCache();
	private InnerNodeCache cacheNodes = new SimpleInnerNodeCache();

	InnerNodeCache getInnerNodeCache()
	{
		return cacheNodes;
	}

	/**
	 * Read an inner node
	 * 
	 * @param depth
	 *            the depth of this node within the tree.
	 */
	private Node readInnerNode(Entry pointingEntry, int depth)
			throws IOException
	{
		Node node = cacheNodes.get(pointingEntry.address, depth);
		if (node != null) {
			return node;
		}
		int nChildren = file.readUnsignedByte();
		List<Entry> entries = new ArrayList<>();
		for (int i = 0; i < nChildren; i++) {
			int x1 = file.readInt();
			int x2 = file.readInt();
			int y1 = file.readInt();
			int y2 = file.readInt();
			int address = file.readInt();
			Entry entry = new Entry(x1, x2, y1, y2, address);
			entries.add(entry);
		}
		node = new Node(false, entries);
		cacheNodes.put(pointingEntry.address, depth, node);
		return node;
	}

	/**
	 * Read an leaf node (point variant)
	 * 
	 * @param depth
	 *            the depth of this node within the tree.
	 */
	private Node readLeafNodePoint(Entry pointingEntry, int depth)
			throws IOException
	{
		Node node = cacheNodes.get(pointingEntry.address, depth);
		if (node != null) {
			return node;
		}
		int nChildren = file.readUnsignedByte();
		List<Entry> entries = new ArrayList<>();
		// compute relative addresses from individual sizes
		int relativeAddress = 0;
		int spanX = pointingEntry.x2 - pointingEntry.x1;
		int spanY = pointingEntry.y2 - pointingEntry.y1;
		for (int i = 0; i < nChildren; i++) {
			int dx, dy;
			if (spanX < 0xff) {
				dx = file.readUnsignedByte();
			} else {
				dx = file.readVariableLengthUnsignedInteger();
			}
			if (spanY < 0xff) {
				dy = file.readUnsignedByte();
			} else {
				dy = file.readVariableLengthUnsignedInteger();
			}
			int size = file.readVariableLengthUnsignedInteger();

			int baseX = pointingEntry.x1;
			int baseY = pointingEntry.y1;

			int x = baseX + dx;
			int y = baseY + dy;

			Entry entry = new Entry(x, x, y, y, relativeAddress);

			entry.size = size;
			entries.add(entry);
			relativeAddress += size;
		}
		// compute absolute addresses from relative addresses
		for (Entry entry : entries) {
			entry.address += file.getFilePointer() - offset;
		}
		node = new Node(true, entries);
		cacheNodes.put(pointingEntry.address, depth, node);
		return node;
	}

	/**
	 * Read an leaf node (general variant)
	 * 
	 * @param depth
	 *            the depth of the node within the tree.
	 */
	private Node readLeafNodeGeneral(Entry pointingEntry, int depth)
			throws IOException
	{
		Node node = cacheNodes.get(pointingEntry.address, depth);
		if (node != null) {
			return node;
		}
		node = readLeafNodeGeneralNonCached(pointingEntry);
		cacheNodes.put(pointingEntry.address, depth, node);
		return node;
	}

	private Node readLeafNodeGeneralNonCached(Entry pointingEntry)
			throws IOException
	{
		int nChildren = file.readUnsignedByte();
		List<Entry> entries = new ArrayList<>();
		// compute relative addresses from individual sizes
		int relativeAddress = 0;
		int spanX = pointingEntry.x2 - pointingEntry.x1;
		int spanY = pointingEntry.y2 - pointingEntry.y1;
		for (int i = 0; i < nChildren; i++) {
			int dx1, dx2, dy1, dy2;
			if (spanX < 0xff) {
				dx1 = file.readUnsignedByte();
				dx2 = file.readUnsignedByte();
			} else {
				dx1 = file.readVariableLengthUnsignedInteger();
				dx2 = file.readVariableLengthUnsignedInteger();
			}
			if (spanY < 0xff) {
				dy1 = file.readUnsignedByte();
				dy2 = file.readUnsignedByte();
			} else {
				dy1 = file.readVariableLengthUnsignedInteger();
				dy2 = file.readVariableLengthUnsignedInteger();
			}
			int size = file.readVariableLengthUnsignedInteger();
			Entry entry = CompressedBoundingBox.decompress(pointingEntry, dx1,
					dx2, dy1, dy2);
			entry.address = relativeAddress;
			entry.size = size;
			entries.add(entry);
			relativeAddress += size;
			// compute absolute addresses from relative addresses
		}
		for (Entry entry : entries) {
			entry.address += file.getFilePointer() - offset;
		}
		return new Node(true, entries);
	}

	/**
	 * Read an element, i.e. an instance of the type of objects stored in this
	 * tree.
	 */
	private T readElement(Entry pointingEntry, int position) throws IOException
	{
		file.seek(offset + position);
		int length = pointingEntry.size;
		byte[] buffer = new byte[length];
		file.readFully(buffer);

		T thing = CastUtil
				.cast(instance.readObject(buffer, pointingEntry, metadata));
		return thing;
	}

	/*
	 * *********************************************************************
	 * Intersection query for tree leafs. Intended for debugging purposes.
	 * *********************************************************************
	 */

	/**
	 * Perform an intersection query on the tree and return a list of entries
	 * pointing to the leafs of this tree that intersect with the requested
	 * bounding box. This method may be useful for debugging purposes, e.g the
	 * tree's leafs may be visualized using this method.
	 * 
	 * @throws IOException
	 */
	public List<Entry> intersectionQueryForLeafs(BoundingBox rectRequest)
			throws IOException
	{
		List<Entry> results = new ArrayList<>();

		ensureRootEntry();

		// set offset to behind initial entry
		int position = POSITION_ROOT_NODE;
		queryLeafs(rootEntry, position, rectRequest, results, 0);

		return results;
	}

	/**
	 * Internal method used for querying leafs.
	 */
	private void queryLeafs(Entry pointingEntry, int position,
			BoundingBox rectRequest, List<Entry> results, int depth)
	{
		// System.out.println("query at position: " + position);
		try {
			Node node = read(pointingEntry, position, depth);
			if (node.isLeaf) {
				results.add(pointingEntry);
				return;
			}
			for (Entry entry : node.entries) {
				if (entry.intersects(rectRequest)) {
					queryLeafs(entry, entry.address, rectRequest, results,
							depth + 1);
				}
			}
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * *********************************************************************
	 * Traversal
	 * *********************************************************************
	 */

	/**
	 * Traversal without intersection test. Just iterate the complete tree.
	 * Traversal is preorder.
	 */
	public void traverse(TraversalHandler<? super T> handler) throws IOException
	{
		file.seek(offset + POSITION_ROOT_ENTRY);
		int x1 = file.readInt();
		int x2 = file.readInt();
		int y1 = file.readInt();
		int y2 = file.readInt();
		int address = file.readInt();
		Entry rootEntry = new Entry(x1, x2, y1, y2, address);

		// set offset to behind initial entry
		int position = POSITION_ROOT_NODE;

		traverse(rootEntry, position, handler, 0);
	}

	/**
	 * Internal method for performing a preorder traversal
	 */
	private void traverse(Entry pointingEntry, int position,
			TraversalHandler<? super T> handler, int depth)
	{
		try {
			// we can only get nodes here, since leaf are caught when
			// reaching their containing leafs
			Node node = read(pointingEntry, position, depth);
			// if this is a leaf, process children directly
			if (node.isLeaf) {
				handler.handleLeaf(pointingEntry, node, depth);
				for (Entry entry : node.entries) {
					T thing = readElement(entry, entry.address);
					handler.handleEntry(entry, thing, depth);
				}
				return;
			}
			// not leaf -> inner node
			handler.handleInnerNode(pointingEntry, node, depth);
			for (Entry entry : node.entries) {
				traverse(entry, entry.address, handler, depth + 1);
			}
		} catch (IOException e) {
			System.out.println("IO exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * *********************************************************************
	 * Access to individual elements of the tree through direct pointers
	 * *********************************************************************
	 * This interface can be used with the values retrieved through the
	 * ValueInsertionCallback and can be used to access individual values stored
	 * in the tree if their exact location is known.
	 */

	public T directQuery(int entryAddress, int childNumber) throws IOException
	{
		file.seek(offset + entryAddress);
		int x1 = file.readInt();
		int x2 = file.readInt();
		int y1 = file.readInt();
		int y2 = file.readInt();
		int address = file.readInt();
		Entry entry = new Entry(x1, x2, y1, y2, address);

		file.seek(offset + address);
		Node leaf = readLeafNodeGeneralNonCached(entry);
		Entry e = leaf.entries.get(childNumber);
		T thing = readElement(e, e.address);

		return thing;
	}
}
