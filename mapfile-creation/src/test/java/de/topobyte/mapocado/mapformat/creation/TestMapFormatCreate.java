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

package de.topobyte.mapocado.mapformat.creation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.mapocado.styles.rules.RuleFileReader;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.osm4j.diskstorage.DbExtensions;
import de.topobyte.osm4j.diskstorage.EntityDbSetup;
import de.topobyte.osm4j.diskstorage.nodedb.NodeDB;
import de.topobyte.osm4j.diskstorage.vardb.VarDB;
import de.topobyte.osm4j.diskstorage.waydb.WayRecordWithTags;
import de.topobyte.osm4j.processing.entities.ExecutableEntityProcessor;
import de.topobyte.osm4j.processing.entities.filter.DefaultEntityFilter;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.simplemapfile.core.EntityFile;
import de.topobyte.simplemapfile.xml.SmxFileReader;
import de.topobyte.system.utils.SystemPaths;

public class TestMapFormatCreate
{

	final static Logger logger = LoggerFactory
			.getLogger(TestMapFormatCreate.class);

	private static Integer[] DEFAULT_LIMITS_NODES = new Integer[] { 12, 14,
			17 };
	private static Integer[] DEFAULT_LIMITS_WAYS = new Integer[] { 12, 14 };
	private static Integer[] DEFAULT_LIMITS_RELATIONS = new Integer[] { 12,
			14 };

	public static void main(String[] args)
			throws ParserConfigurationException, SAXException, IOException
	{
		Path output = Paths.get("/tmp/test-mapfile.xmap");
		Path fileInput = Paths.get(
				"/data/oxygen/selenium-extracts/planet-180813/germany/Schwerin.tbo");
		OsmFileInput input = new OsmFileInput(fileInput, FileFormat.TBO);
		Path fileBoundary = Paths.get(
				"/data/oxygen/selenium-extracts/planet-180813/germany/Schwerin.smx");
		Path configDir = SystemPaths.CWD.getParent().getParent()
				.resolve("config/citymaps/rendertheme-v2/rules/default");

		Path tempDir = java.nio.file.Files
				.createTempDirectory("mapocado-mapfile-creation");

		Path nodeIndex = tempDir
				.resolve("nodes" + DbExtensions.EXTENSION_INDEX);
		Path nodeData = tempDir.resolve("nodes" + DbExtensions.EXTENSION_DATA);
		Path wayIndex = tempDir.resolve("ways" + DbExtensions.EXTENSION_INDEX);
		Path wayData = tempDir.resolve("ways" + DbExtensions.EXTENSION_DATA);

		logger.info("nodedb index: " + nodeIndex);
		logger.info("nodedb data: " + nodeData);
		logger.info("waydb index: " + wayIndex);
		logger.info("waydb data: " + wayData);

		try {
			EntityDbSetup.createNodeDb(fileInput, nodeIndex, nodeData);
		} catch (IOException e) {
			logger.error("errror while creating node database", e);
			System.exit(1);
		}

		try {
			EntityDbSetup.createWayDb(fileInput, wayIndex, wayData, true);
		} catch (IOException e) {
			logger.error("errror while creating way database", e);
			System.exit(1);
		}

		List<Integer> limitsNodes = Arrays.asList(DEFAULT_LIMITS_NODES);
		List<Integer> limitsWays = Arrays.asList(DEFAULT_LIMITS_WAYS);
		List<Integer> limitsRelations = Arrays.asList(DEFAULT_LIMITS_RELATIONS);

		EntityFile boundary = SmxFileReader.read(fileBoundary);
		Geometry land = null;
		Path logsDir = tempDir.resolve("logs");
		Files.createDirectories(logsDir);

		RuleSet config = new RuleSet();
		try {
			File[] ruleFiles = configDir.toFile().listFiles();
			for (File ruleFile : ruleFiles) {
				if (!ruleFile.getName().endsWith(".xml")) {
					continue;
				}
				RuleFileReader.read(config, ruleFile.getPath());
			}
		} catch (Exception e) {
			logger.error("Exception while parsing rules: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		NodeDB nodeDB = null;
		VarDB<WayRecordWithTags> wayDB = null;
		try {
			nodeDB = new NodeDB(nodeData, nodeIndex);
			wayDB = new VarDB<>(wayData, wayIndex, new WayRecordWithTags(0));
		} catch (FileNotFoundException e) {
			// do nothing
		}

		MapformatCreator task = new MapformatCreator(output.toFile(), config,
				input, input, input, nodeDB, boundary.getGeometry(),
				logsDir.toFile(), land, limitsNodes, limitsWays,
				limitsRelations);
		ExecutableEntityProcessor processor = new ExecutableEntityProcessor(
				task, nodeDB, wayDB, boundary.getGeometry(), logsDir,
				new DefaultEntityFilter());

		processor.prepare();
		task.prepare();

		processor.execute(input, input, input);

		task.createFile();
	}

}
