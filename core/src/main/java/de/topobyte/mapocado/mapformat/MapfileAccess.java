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

package de.topobyte.mapocado.mapformat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.interval.NaiveIntervalTree;
import de.topobyte.mapocado.mapformat.io.FilePartition;
import de.topobyte.mapocado.mapformat.io.Header;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.model.TextNode;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.randomaccess.FileAccess;
import de.topobyte.randomaccess.InputStreamFileAccess;
import de.topobyte.randomaccess.RandomAccessFileAccess;

public class MapfileAccess
{
	final static Logger logger = LoggerFactory.getLogger(MapfileAccess.class);

	public static Mapfile open(File target)
			throws IOException, ClassNotFoundException
	{
		logger.info("loading index: " + target);

		RandomAccessFile raf = new RandomAccessFile(target, "r");
		FileAccess file = new RandomAccessFileAccess(raf);

		return open(file);
	}

	public static Mapfile open(InputStream target)
			throws IOException, ClassNotFoundException
	{
		logger.info("loading index: " + target);

		FileAccess file = new InputStreamFileAccess(target);

		return open(file);
	}

	public static Mapfile open(FileAccess file)
			throws IOException, ClassNotFoundException
	{
		Header header = null;
		Metadata metadata = null;
		IntervalTree<Integer, DiskTree<Node>> treeNodes = null;
		IntervalTree<Integer, DiskTree<Way>> treeWays = null;
		IntervalTree<Integer, DiskTree<Relation>> treeRelations = null;

		int position = 0;

		header = new Header();
		position += header.read(file, position);

		metadata = new Metadata();
		metadata.read(file, position);
		FilePartition partition = metadata.getFilePartition();

		logger.info("file partition: " + partition);

		List<Integer> positions = partition.getPositions();

		int k = 0;
		List<Integer> intervalsNodes = metadata.getIntervalsNodes();
		List<Integer> intervalsWays = metadata.getIntervalsWays();
		List<Integer> intervalsRelations = metadata.getIntervalsRelations();

		List<DiskTree<Way>> wayTrees = new ArrayList<>();
		List<DiskTree<Relation>> relationTrees = new ArrayList<>();
		List<DiskTree<Node>> nodeTrees = new ArrayList<>();

		for (int i = 0; i <= intervalsWays.size(); i++) {
			logger.info("w position: " + positions.get(k));
			DiskTree<Way> tree = new DiskTree<>(file, positions.get(k),
					new Way(), metadata, false);
			wayTrees.add(tree);
			k++;
		}
		for (int i = 0; i <= intervalsRelations.size(); i++) {
			logger.info("r position: " + positions.get(k));
			DiskTree<Relation> tree = new DiskTree<>(file, positions.get(k),
					new Relation(), metadata, false);
			relationTrees.add(tree);
			k++;
		}
		for (int i = 0; i <= intervalsNodes.size(); i++) {
			logger.info("n position: " + positions.get(k));
			DiskTree<Node> tree = new DiskTree<>(file, positions.get(k),
					new Node(), metadata, true);
			nodeTrees.add(tree);
			k++;
		}

		treeWays = new NaiveIntervalTree<>(intervalsWays, wayTrees);
		treeRelations = new NaiveIntervalTree<>(intervalsRelations,
				relationTrees);
		treeNodes = new NaiveIntervalTree<>(intervalsNodes, nodeTrees);

		DiskTree<TextNode> treeHousenumbers = new DiskTree<>(file,
				positions.get(k), new TextNode(), metadata, true);

		Mapfile mapfile = new Mapfile(file, header, metadata, treeNodes,
				treeWays, treeRelations, treeHousenumbers);

		return mapfile;
	}

}
