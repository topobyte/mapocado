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

package de.topobyte.mapocado.mapformat.mapfile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class PrintIntervals
{

	final static Logger logger = LoggerFactory.getLogger(PrintIntervals.class);

	private static final String OPTION_INPUT = "input";

	public static void main(String name, String[] args)
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
			logger.error("while opening mapfile", e);
			System.exit(1);
		} catch (IOException e) {
			logger.error("while opening mapfile", e);
			System.exit(1);
		}

		List<Integer> startsNodes = mapfile.getTreeNodes().getIntervalStarts();
		System.out.println("nodes: " + startsNodes);

		List<Integer> startsWays = mapfile.getTreeWays().getIntervalStarts();
		System.out.println("ways: " + startsWays);

		List<Integer> startsRelations = mapfile.getTreeRelations()
				.getIntervalStarts();
		System.out.println("relations: " + startsRelations);
	}

}
