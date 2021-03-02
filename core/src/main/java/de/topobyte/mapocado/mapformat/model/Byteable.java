package de.topobyte.mapocado.mapformat.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.topobyte.mapocado.mapformat.rtree.disk.Entry;

public interface Byteable
{

	public void clear();

	public void read(InputStream is, Entry entry, Object metadata)
			throws IOException;

	public int read(byte[] buffer, Entry entry, Object metadata)
			throws IOException;

	public void write(OutputStream os, Entry entry, Object metadata)
			throws IOException;

	public Byteable readObject(byte[] buffer, Entry entry, Object metadata)
			throws IOException;
}
