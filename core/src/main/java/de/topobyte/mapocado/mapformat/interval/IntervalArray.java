package de.topobyte.mapocado.mapformat.interval;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import de.topobyte.randomaccess.FileAccess;

public class IntervalArray extends ArrayList<Integer>
{

	private static final long serialVersionUID = 142959554037998204L;

	public IntervalArray()
	{
		super();
	}

	public IntervalArray(Integer... initialElements)
	{
		super();
		for (Integer element : initialElements) {
			add(element);
		}
	}

	public int read(FileAccess file) throws IOException, ClassNotFoundException
	{
		long startPosition = file.getFilePointer();
		int numIntervals = file.readUnsignedByte();
		// System.out.println("intervals: " + numIntervals);
		for (int i = 0; i < numIntervals; i++) {
			int interval = file.readUnsignedByte();
			add(interval);
		}
		return (int) (file.getFilePointer() - startPosition);
	}

	public int write(RandomAccessFile file) throws IOException
	{
		file.writeByte(size());
		for (int i = 0; i < size(); i++) {
			file.writeByte(get(i));
		}
		return 1 + size();
	}
}
