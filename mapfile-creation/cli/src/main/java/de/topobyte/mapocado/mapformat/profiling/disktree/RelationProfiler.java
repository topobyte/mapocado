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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.rtree.disk.DefaultTraversalHandler;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class RelationProfiler
{

	static final Logger logger = LoggerFactory
			.getLogger(RelationProfiler.class);

	private static final String OPTION_INPUT = "input";

	public static void main(String name, String[] args) throws IOException
	{
		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, true, "a serialization of a rtree");
		// @formatter:on

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
			mapfile = MapFileAccess.open(new File(inputFile));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		RelationProfiler profiler = new RelationProfiler(mapfile);
		profiler.profile();
	}

	private final Mapfile mapfile;

	public RelationProfiler(Mapfile mapfile)
	{
		this.mapfile = mapfile;
	}

	private void profile() throws IOException
	{
		IntervalTree<Integer, DiskTree<Relation>> treeRelations = mapfile
				.getTreeRelations();

		List<Integer> intervals = treeRelations.getIntervalStarts();
		for (int i = -1; i < intervals.size(); i++) {
			int zoom = i < 0 ? 0 : intervals.get(i);
			System.out.println("PROFILING TREE. zoom: " + zoom);
			DiskTree<Relation> tree = treeRelations.getObject(zoom);
			EntityTraversalHandler handler = new EntityTraversalHandler();
			tree.traverse(handler);
			print(handler);
		}
	}

	private void print(EntityTraversalHandler handler)
	{
		Collections.sort(handler.relations, new Comparator<Relation>() {

			@Override
			public int compare(Relation o1, Relation o2)
			{
				return o1.getPolygon().getNumberOfCoordinates()
						- o2.getPolygon().getNumberOfCoordinates();
			}
		});

		Multiset<Integer> sizeCounter = TreeMultiset.create();
		for (Relation relation : handler.relations) {
			Multipolygon polygon = relation.getPolygon();
			sizeCounter.add(polygon.getNumberOfCoordinates());
		}

		for (int size : sizeCounter.elementSet()) {

			System.out.println(
					String.format("# %d: %d", size, sizeCounter.count(size)));
		}
	}

	private class EntityTraversalHandler
			extends DefaultTraversalHandler<Relation>
	{

		List<Relation> relations = new ArrayList<>();

		@Override
		public void handleEntry(
				de.topobyte.mapocado.mapformat.rtree.disk.Entry entry,
				Relation relation, int level)
		{
			relations.add(relation);
		}
	}

}
