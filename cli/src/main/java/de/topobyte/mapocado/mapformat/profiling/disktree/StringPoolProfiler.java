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

package de.topobyte.mapocado.mapformat.profiling.disktree;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;

import de.topobyte.mapocado.mapformat.MapfileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DefaultTraversalHandler;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class StringPoolProfiler
{

	static final Logger logger = LoggerFactory
			.getLogger(StringPoolProfiler.class);

	private static final String OPTION_INPUT = "input";

	public static void main(String[] args) throws IOException
	{
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, true, "an input file");

		CommandLine line = null;
		try {
			line = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			System.out
					.println("unable to parse command line: " + e.getMessage());
		}

		if (line == null) {
			return;
		}

		String inputFile = line.getOptionValue(OPTION_INPUT);

		Mapfile mapfile = null;
		try {
			mapfile = MapfileAccess.open(new File(inputFile));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		StringPoolProfiler profiler = new StringPoolProfiler(mapfile);
		profiler.profile();
	}

	private final Mapfile mapfile;

	public StringPoolProfiler(Mapfile mapfile)
	{
		this.mapfile = mapfile;
	}

	private Multiset<Integer> counter = HashMultiset.create();

	private void profile() throws IOException
	{
		StringPool poolForRefs = mapfile.getMetadata().getPoolForRefs();
		int n = poolForRefs.getNumberOfEntries();

		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();
		IntervalTree<Integer, DiskTree<Node>> treeNodes = mapfile
				.getTreeNodes();

		List<Integer> intervalsWays = treeWays.getIntervalStarts();
		List<Integer> intervalsNodes = treeNodes.getIntervalStarts();

		for (int i = -1; i < intervalsWays.size(); i++) {
			int zoom = i < 0 ? 0 : intervalsWays.get(i);
			System.out.println("zoom: " + zoom);
			DiskTree<Way> tree = treeWays.getObject(zoom);
			EntityTraversalHandler handler = new EntityTraversalHandler();
			tree.traverse(handler);
		}

		for (int i = -1; i < intervalsNodes.size(); i++) {
			int zoom = i < 0 ? 0 : intervalsNodes.get(i);
			System.out.println("zoom: " + zoom);
			DiskTree<Node> tree = treeNodes.getObject(zoom);
			EntityTraversalHandler handler = new EntityTraversalHandler();
			tree.traverse(handler);
		}

		for (int i = 0; i < n; i++) {
			String string = poolForRefs.getString(i);
			int occs = counter.count(i);
			System.out.println(String.format(
					"class #%d: name: %s, occurences: %d", i, string, occs));
		}
	}

	private class EntityTraversalHandler extends DefaultTraversalHandler<Entity>
	{

		@Override
		public void handleEntry(Entry entry, Entity thing, int level)
		{
			TIntArrayList classes = thing.getClasses();
			for (int c : classes.toArray()) {
				counter.add(c);
			}
		}
	}

}
