package de.topobyte.mapocado.swing.viewer;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.ConfigBundleReader;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunMapStyleEditor
{
	static final Logger logger = LoggerFactory
			.getLogger(RunMapStyleEditor.class);

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_CONFIG = "config";
	private static final String OPTION_START = "start";
	private static final String OPTION_ZOOM = "zoom";
	private static final String OPTION_GRID = "grid";
	private static final String OPTION_TILENUMBERS = "tile_numbers";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			// @formatter:off
			Options options = new Options();
			OptionHelper.addL(options, OPTION_INPUT, true, true, "a serialization of a rtree");
			OptionHelper.addL(options, OPTION_CONFIG, true, true, "a rendering configuration");
			OptionHelper.addL(options, OPTION_START, true, false, "a start position");
			OptionHelper.addL(options, OPTION_ZOOM, true, false, "a start zoom");

			OptionHelper.addL(options, OPTION_GRID, true, false, "yes/no", "show grid?");
			OptionHelper.addL(options, OPTION_TILENUMBERS, true, false, "yes/no", "show tile numbers?");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	/**
	 * @param args
	 *            input, output_index, output_data
	 */
	public static void main(String name, CommonsCliArguments arguments)
	{
		CommandLine line = arguments.getLine();

		String inputFile = line.getOptionValue(OPTION_INPUT);
		String configFile = line.getOptionValue(OPTION_CONFIG);

		boolean hasStartPosition = false;
		double startUpLon = 0.0;
		double startUpLat = 0.0;
		if (line.hasOption(OPTION_START)) {
			hasStartPosition = true;
			String start = line.getOptionValue(OPTION_START);
			String[] coords = start.split(",");
			if (coords.length != 2) {
				System.out.println("number format not supported: " + start);
				System.exit(1);
			}
			NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
			try {
				startUpLon = numberFormat.parse(coords[0]).doubleValue();
			} catch (java.text.ParseException e) {
				System.out.println("unable to parse number: " + coords[0]);
				System.exit(1);
			}
			try {
				startUpLat = numberFormat.parse(coords[1]).doubleValue();
			} catch (java.text.ParseException e) {
				System.out.println("unable to parse number: " + coords[1]);
				System.exit(1);
			}
			System.out.println(
					"startup position: " + startUpLon + ", " + startUpLat);
		}

		boolean hasStartZoom = false;
		int startUpZoom = 1;
		if (line.hasOption(OPTION_ZOOM)) {
			hasStartZoom = true;
			String zoom = line.getOptionValue(OPTION_ZOOM);
			NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
			try {
				startUpZoom = numberFormat.parse(zoom).intValue();
			} catch (java.text.ParseException e) {
				System.out.println("unable to parse number: " + zoom);
				System.exit(1);
			}
			System.out.println("startup zoom: " + startUpZoom);
		}

		boolean drawTileBorders = false;
		if (line.hasOption(OPTION_GRID)) {
			String value = line.getOptionValue(OPTION_GRID);
			if (value.equalsIgnoreCase("yes")) {
				drawTileBorders = true;
			}
		}

		boolean drawTileNumbers = false;
		if (line.hasOption(OPTION_TILENUMBERS)) {
			String value = line.getOptionValue(OPTION_TILENUMBERS);
			if (value.equalsIgnoreCase("yes")) {
				drawTileNumbers = true;
			}
		}

		MapStyleEditor mapStyleEditor = new MapStyleEditor();

		logger.info("reading style package: " + configFile);
		ConfigBundle configBundle = null;
		try {
			configBundle = ConfigBundleReader
					.readConfigBundle(new File(configFile));
		} catch (IOException e) {
			logger.error("unable to read style package (IOException): "
					+ e.getMessage(), e);
			System.exit(1);
		} catch (InvalidBundleException e) {
			logger.error("unable to read style package (Invalid bundle): "
					+ e.getMessage(), e);
			System.exit(1);
		}

		if (hasStartPosition) {
			mapStyleEditor.setStartPosition(startUpLon, startUpLat);
		}
		if (hasStartZoom) {
			mapStyleEditor.setStartZoom(startUpZoom);
		}

		try {
			mapStyleEditor.setup(new File(inputFile), configBundle,
					drawTileBorders, drawTileNumbers);
		} catch (IOException e) {
			System.out.println("error setting up viewer: " + e.getMessage());
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.out.println("error setting up viewer: " + e.getMessage());
			System.exit(1);
		} catch (ParserConfigurationException e) {
			System.out.println("error setting up viewer: " + e.getMessage());
			System.exit(1);
		} catch (SAXException e) {
			System.out.println("error setting up viewer: " + e.getMessage());
			System.exit(1);
		}
	}
}
