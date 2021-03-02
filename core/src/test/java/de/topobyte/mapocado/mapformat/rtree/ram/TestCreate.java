package de.topobyte.mapocado.mapformat.rtree.ram;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Random;

import com.infomatiq.jsi.Rectangle;

import de.topobyte.jsi.GenericRTree;
import de.topobyte.mapocado.mapformat.model.Byteable;
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;
import de.topobyte.mapocado.mapformat.rtree.disk.GeneralDiskTreeCreator;
import de.topobyte.mapocado.mapformat.util.ByteInputStream;
import de.topobyte.mapocado.mapformat.util.CompactReaderInputStream;
import de.topobyte.mapocado.mapformat.util.CompactWriter;

public class TestCreate
{

	public static void main(String[] args)
			throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, IOException
	{
		Random random = new Random();
		GenericRTree<ByteableInteger> tree = new GenericRTree<>(2, 4);
		for (int i = 0; i < 10; i++) {
			float x = random.nextFloat();
			float y = random.nextFloat();
			ByteableInteger value = new ByteableInteger(random.nextInt());
			tree.add(new Rectangle(x, y, x, y), value);
		}

		IRTree<ByteableInteger> ramRTree = Converter.create(tree);

		// List<Node> nodes = NodeIndexer.buildFromRamNode(ramRTree.getRoot());
		// DiskRTree<Integer> diskTree = DiskRTree.fillFromRamTree(ramRTree);
		// System.out.println(diskTree.nodeList.size());

		RandomAccessFile file = new RandomAccessFile("/tmp/foo.r", "rw");
		GeneralDiskTreeCreator.create(file, 0, ramRTree, null, null);
		file.close();
	}

	private static class ByteableInteger implements Byteable
	{

		private int n;

		public ByteableInteger(int n)
		{
			this.n = n;
		}

		@Override
		public void read(InputStream is, Entry entry, Object metadata)
				throws IOException
		{
			CompactReaderInputStream reader = new CompactReaderInputStream(is);
			n = reader.readInt32();
		}

		@Override
		public void write(OutputStream os, Entry entry, Object metadata)
				throws IOException
		{
			CompactWriter writer = new CompactWriter(os);
			writer.writeInt32(n);
		}

		@Override
		public Byteable readObject(byte[] buffer, Entry entry, Object metadata)
				throws IOException
		{
			ByteableInteger value = new ByteableInteger(0);
			ByteInputStream is = new ByteInputStream(buffer);
			value.read(is, entry, metadata);
			return value;
		}

		@Override
		public void clear()
		{
			// no-op
		}

		@Override
		public int read(byte[] buffer, Entry entry, Object metadata)
				throws IOException
		{
			ByteInputStream is = new ByteInputStream(buffer);
			read(is, entry, metadata);
			return buffer.length;
		}

	}

}
