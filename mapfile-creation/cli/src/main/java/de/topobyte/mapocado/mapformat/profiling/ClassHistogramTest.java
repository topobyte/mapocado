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

package de.topobyte.mapocado.mapformat.profiling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.preprocess.classhistogram.ClassHistogramBuilder;
import de.topobyte.mapocado.styles.rules.RuleFileReader;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.osm4j.diskstorage.nodedb.NodeDB;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class ClassHistogramTest
{

	static final Logger logger = LoggerFactory
			.getLogger(ClassHistogramTest.class);

	private static final String OPTION_NODES_INDEX = "nodes-index";
	private static final String OPTION_NODES_DATA = "nodes-data";
	private static final String OPTION_NODES_FILE = "nodes";
	private static final String OPTION_WAYS_FILE = "ways";
	private static final String OPTION_RELATIONS_FILE = "relations";
	private static final String OPTION_CONFIG = "config";

	/**
	 * @param args
	 *            input, output_index, output_data
	 */
	public static void main(String name, String[] args) throws IOException
	{
		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_NODES_INDEX, true, true, "a node database");
		OptionHelper.addL(options, OPTION_NODES_DATA, true, true, "a node database");
		OptionHelper.addL(options, OPTION_NODES_FILE, true, true, "a osm file containing the nodes");
		OptionHelper.addL(options, OPTION_WAYS_FILE, true, true, "a osm file containing the ways");
		OptionHelper.addL(options, OPTION_RELATIONS_FILE, true, true, "a osm file containing the relations");
		OptionHelper.addL(options, OPTION_CONFIG, true, true, "a render config directory");
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

		Path nodesIndex = Paths.get(line.getOptionValue(OPTION_NODES_INDEX));
		Path nodesData = Paths.get(line.getOptionValue(OPTION_NODES_DATA));
		String nodesPath = line.getOptionValue(OPTION_NODES_FILE);
		String waysPath = line.getOptionValue(OPTION_WAYS_FILE);
		String relationsPath = line.getOptionValue(OPTION_RELATIONS_FILE);
		String configDir = line.getOptionValue(OPTION_CONFIG);

		FileFormat inputFormat = FileFormat.TBO;
		OsmFileInput nodesFile = new OsmFileInput(Paths.get(nodesPath),
				inputFormat);
		OsmFileInput waysFile = new OsmFileInput(Paths.get(waysPath),
				inputFormat);
		OsmFileInput relationsFile = new OsmFileInput(Paths.get(relationsPath),
				inputFormat);

		RuleSet config = new RuleSet();
		try {
			File dirRuleFiles = new File(configDir);
			File[] ruleFiles = dirRuleFiles.listFiles();
			for (File ruleFile : ruleFiles) {
				if (!ruleFile.getName().endsWith(".xml")) {
					continue;
				}
				RuleFileReader.read(config, ruleFile.getPath());
			}
		} catch (Exception e) {
			logger.error("Exception while parsing rules: " + e.getMessage());
			e.printStackTrace();
		}

		NodeDB nodeDB = null;
		try {
			nodeDB = new NodeDB(nodesData, nodesIndex);
		} catch (FileNotFoundException e) {
			// do nothing
		}

		if (nodeDB == null) {
			return;
		}

		ClassHistogramBuilder test = new ClassHistogramBuilder(config,
				nodesFile, waysFile, relationsFile, nodeDB);
		test.execute();
		StringPool stringPool = test.createClassStringPool();
		System.out.println(String.format("String pool size: %d",
				stringPool.getNumberOfEntries()));
	}

}
