package de.topobyte.mapocado.mapformat.mapfile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.mapformat.creation.MapfileCreator;
import de.topobyte.osm4j.diskstorage.DbExtensions;
import de.topobyte.osm4j.diskstorage.EntityDbSetup;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class CreateMapfile
{

	final static Logger logger = LoggerFactory
			.getLogger(CreateMapfile.class);

	final static String HELP_MESSAGE = CreateMapfile.class.getSimpleName()
			+ " [options]";

	private static final String OPTION_NODE_DB = "node-db";
	private static final String OPTION_WAY_DB = "way-db";
	private static final String OPTION_INPUT = "input";
	private static final String OPTION_NODES_FILE = "nodes";
	private static final String OPTION_WAYS_FILE = "ways";
	private static final String OPTION_RELATIONS_FILE = "relations";
	private static final String OPTION_RULES = "rules";
	private static final String OPTION_OUTPUT = "output";
	private static final String OPTION_BOUNDARY = "boundary";
	private final static String OPTION_LOGS = "logs";
	private static final String OPTION_LAND_GEOMETRY = "land-geometry";
	private static final String OPTION_LIMITS_NODES = "limits-nodes";
	private static final String OPTION_LIMITS_WAYS = "limits-ways";
	private static final String OPTION_LIMITS_RELATIONS = "limits-relations";

	/**
	 * @param args
	 *            input, output_index, output_data
	 */
	public static void main(String name, String[] args)
	{
		String precedenceExplanation = " (takes precedence over the file specified via -"
				+ OPTION_INPUT + ")";
		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, false, "file", "an osm input file");
		OptionHelper.addL(options, OPTION_NODES_FILE, true, false, "file", "an osm file containing the nodes" + precedenceExplanation);
		OptionHelper.addL(options, OPTION_WAYS_FILE, true, false, "file", "an osm file containing the ways" + precedenceExplanation);
		OptionHelper.addL(options, OPTION_RELATIONS_FILE, true, false, "file", "an osm file containing the relations" + precedenceExplanation);
		OptionHelper.addL(options, OPTION_NODE_DB, true, false, "a node database");
		OptionHelper.addL(options, OPTION_WAY_DB, true, false, "a way database");
		OptionHelper.addL(options, OPTION_OUTPUT, true, true, "file", "a file to put results to");
		OptionHelper.addL(options, OPTION_RULES, true, true, "directory", "where to look for rule definitions");
		OptionHelper.addL(options, OPTION_BOUNDARY, true, true, "file", "a boundary file to cut objects with");
		OptionHelper.addL(options, OPTION_LOGS, true, true, "directory", "where to dump debug information");
		OptionHelper.addL(options, OPTION_LAND_GEOMETRY, true, false, "file", "a geometry representing the land to generate sea");
		OptionHelper.addL(options, OPTION_LIMITS_NODES, true, false, "a comma separated list of integers");
		OptionHelper.addL(options, OPTION_LIMITS_WAYS, true, false, "a comma separated list of integers");
		OptionHelper.addL(options, OPTION_LIMITS_RELATIONS, true, false, "a comma separated list of integers");
		// @formatter:on

		CommandLine line = null;
		try {
			line = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			System.out
					.println("unable to parse command line: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.setOptionComparator(null);
			formatter.printHelp(HELP_MESSAGE, options);
			System.exit(1);
		}

		if (line == null) {
			return;
		}

		// @formatter:off
		String argBoundaryFile = line.getOptionValue(OPTION_BOUNDARY);
		String argNodeDb = line.getOptionValue(OPTION_NODE_DB);
		String argWayDb = line.getOptionValue(OPTION_WAY_DB);
		String argInput = line.getOptionValue(OPTION_INPUT);
		String argNodes = line.getOptionValue(OPTION_NODES_FILE);
		String argWays = line.getOptionValue(OPTION_WAYS_FILE);
		String argRelations = line.getOptionValue(OPTION_RELATIONS_FILE);
		String argOutput = line.getOptionValue(OPTION_OUTPUT);
		String argRules = line.getOptionValue(OPTION_RULES);
		String argLog = line.getOptionValue(OPTION_LOGS);
		String landGeometryFile = line.getOptionValue(OPTION_LAND_GEOMETRY);
		String limitsNodesString = line.getOptionValue(OPTION_LIMITS_NODES);
		String limitsWaysString = line.getOptionValue(OPTION_LIMITS_WAYS);
		String limitsRelationsString = line.getOptionValue(OPTION_LIMITS_RELATIONS);
		// @formatter:on

		if (argInput == null) {
			if (argNodes == null) {
				System.out.println("Please specify an -" + OPTION_INPUT
						+ " paramter or a -" + OPTION_NODES_FILE
						+ " parameter");
				System.exit(1);
			}
			if (argWays == null) {
				System.out.println("Please specify an -" + OPTION_INPUT
						+ " paramter or a -" + OPTION_WAYS_FILE + " parameter");
				System.exit(1);
			}
			if (argRelations == null) {
				System.out.println("Please specify an -" + OPTION_INPUT
						+ " paramter or a -" + OPTION_RELATIONS_FILE
						+ " parameter");
				System.exit(1);
			}
		}

		Path pathNodes = argNodes != null ? Paths.get(argNodes)
				: Paths.get(argInput);
		Path pathWays = argWays != null ? Paths.get(argWays)
				: Paths.get(argInput);
		Path pathRelations = argRelations != null ? Paths.get(argRelations)
				: Paths.get(argInput);

		FileFormat inputFormat = FileFormat.TBO;
		OsmFileInput inputNodes = new OsmFileInput(pathNodes, inputFormat);
		OsmFileInput inputWays = new OsmFileInput(pathWays, inputFormat);
		OsmFileInput inputRelations = new OsmFileInput(pathRelations,
				inputFormat);

		Path pathBoundaryFile = Paths.get(argBoundaryFile);
		Path pathRules = Paths.get(argRules);
		Path pathOutput = Paths.get(argOutput);

		Path nodeIndex = Paths.get(argNodeDb + DbExtensions.EXTENSION_INDEX);
		Path nodeData = Paths.get(argNodeDb + DbExtensions.EXTENSION_DATA);
		Path wayIndex = Paths.get(argWayDb + DbExtensions.EXTENSION_INDEX);
		Path wayData = Paths.get(argWayDb + DbExtensions.EXTENSION_DATA);

		logger.info("nodedb index: " + nodeIndex);
		logger.info("nodedb data: " + nodeData);
		logger.info("waydb index: " + wayIndex);
		logger.info("waydb data: " + wayData);

		try {
			EntityDbSetup.createNodeDb(pathNodes, nodeIndex, nodeData);
		} catch (IOException e) {
			logger.error("errror while creating node database", e);
			System.exit(1);
		}

		try {
			EntityDbSetup.createWayDb(pathWays, wayIndex, wayData, true);
		} catch (IOException e) {
			logger.error("errror while creating way database", e);
			System.exit(1);
		}

		Path pathLogs = argLog == null ? null : Paths.get(argLog);
		Path landFile = landGeometryFile == null ? null
				: Paths.get(landGeometryFile);

		MapfileCreator creator = MapfileCreator.setup(pathBoundaryFile, nodeIndex,
				nodeData, wayIndex, wayData, inputNodes, inputWays,
				inputRelations, pathOutput, pathRules, pathLogs, landFile,
				limitsNodesString, limitsWaysString, limitsRelationsString);

		if (creator == null) {
			logger.error("error while setting up creator instance");
			System.exit(1);
		}

		try {
			creator.execute();
		} catch (IOException e) {
			logger.error("error while creating file", e);
			System.exit(1);
		}
	}

}
