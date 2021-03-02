package de.topobyte.mapocado.mapformat;

import java.io.IOException;

import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.io.Header;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.model.TextNode;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.randomaccess.FileAccess;

public class Mapfile
{

	private final FileAccess file;
	private final Header header;
	private Metadata metadata;
	private IntervalTree<Integer, DiskTree<Node>> treeNodes;
	private IntervalTree<Integer, DiskTree<Way>> treeWays;
	private IntervalTree<Integer, DiskTree<Relation>> treeRelations;
	private DiskTree<TextNode> treeHousenumbers;

	public Mapfile(FileAccess file, Header header, Metadata metadata,
			IntervalTree<Integer, DiskTree<Node>> treeNodes,
			IntervalTree<Integer, DiskTree<Way>> treeWays,
			IntervalTree<Integer, DiskTree<Relation>> treeRelations,
			DiskTree<TextNode> treeHousenumbers)
	{
		this.file = file;
		this.header = header;
		this.metadata = metadata;
		this.treeNodes = treeNodes;
		this.treeWays = treeWays;
		this.treeRelations = treeRelations;
		this.treeHousenumbers = treeHousenumbers;
	}

	public FileAccess getFile()
	{
		return file;
	}

	public Header getHeader()
	{
		return header;
	}

	public Metadata getMetadata()
	{
		return metadata;
	}

	public IntervalTree<Integer, DiskTree<Node>> getTreeNodes()
	{
		return treeNodes;
	}

	public IntervalTree<Integer, DiskTree<Way>> getTreeWays()
	{
		return treeWays;
	}

	public IntervalTree<Integer, DiskTree<Relation>> getTreeRelations()
	{
		return treeRelations;
	}

	public DiskTree<TextNode> getTreeHousenumbers()
	{
		return treeHousenumbers;
	}

	public void close() throws IOException
	{
		file.close();
	}

}
