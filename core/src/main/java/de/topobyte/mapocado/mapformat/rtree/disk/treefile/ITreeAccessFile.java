package de.topobyte.mapocado.mapformat.rtree.disk.treefile;

import java.io.IOException;

public interface ITreeAccessFile
{
	/*
	 * positioning methods
	 */

	public void seek(long i) throws IOException;

	public long getFilePointer() throws IOException;

	/*
	 * primitive data access
	 */

	public int read() throws IOException;

	public float readFloat() throws IOException;

	public int readInt() throws IOException;

	public int readUnsignedByte() throws IOException;

	public void readFully(byte[] buffer) throws IOException;

	/*
	 * CompactReader style access
	 */

	public int readVariableLengthUnsignedInteger() throws IOException;
}
