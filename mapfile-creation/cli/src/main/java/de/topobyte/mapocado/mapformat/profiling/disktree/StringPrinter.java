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

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DefaultTraversalHandler;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class StringPrinter
{

	static final Logger logger = LoggerFactory.getLogger(StringPrinter.class);

	private static final String OPTION_INPUT = "input";

	public static void main(String name, String[] args) throws IOException
	{
		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, true, "an input file");
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

		StringPrinter profiler = new StringPrinter(mapfile);
		profiler.profile();
	}

	private Mapfile mapfile;

	public StringPrinter(Mapfile mapfile)
	{
		this.mapfile = mapfile;
	}

	private void profile() throws IOException
	{
		WayTraversalHandler handler = new WayTraversalHandler();
		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();
		List<Integer> intervals = treeWays.getIntervalStarts();

		for (int i = -1; i < intervals.size(); i++) {
			int zoom = i < 0 ? 0 : intervals.get(i);
			DiskTree<Way> tree = treeWays.getObject(zoom);
			tree.traverse(handler);
		}
	}

	private class WayTraversalHandler extends DefaultTraversalHandler<Way>
	{

		@Override
		public void handleEntry(
				de.topobyte.mapocado.mapformat.rtree.disk.Entry entry,
				Way thing, int level)
		{
			TIntObjectHashMap<String> tags = thing.getTags();
			for (int key : tags.keys()) {
				mapfile.getMetadata().getPoolForKeepKeys().getString(key);
				String value = tags.get(key);
				System.out.print(value);
			}
		}
	}

}
