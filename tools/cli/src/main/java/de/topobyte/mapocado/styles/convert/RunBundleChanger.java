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

package de.topobyte.mapocado.styles.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.ConfigBundleReader;
import de.topobyte.mapocado.styles.bundled.ConfigBundleWriter;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

/**
 * Manipulate a style configuration by applying some transformation to each
 * colour. This program operates on a style bundles.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 */
public class RunBundleChanger
{
	static final Logger logger = LoggerFactory
			.getLogger(RunBundleChanger.class);

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT = "output";

	private static final String OPTION_MODE = "mode";
	private static final String OPTION_ANGLE = "angle";

	private static final String MODE_INVERT_RGB = "invert_rgb";
	private static final String MODE_INVERT_LUMINANCE = "invert_luminance";
	private static final String MODE_INVERT_RGB_LUMINANCE = "invert_rgb_luminance";
	private static final String MODE_ROTATE_HUE = "rotate_hue";

	private static Set<String> possibleModes = new HashSet<>();
	static {
		possibleModes.add(MODE_INVERT_RGB);
		possibleModes.add(MODE_INVERT_LUMINANCE);
		possibleModes.add(MODE_INVERT_RGB_LUMINANCE);
		possibleModes.add(MODE_ROTATE_HUE);
	}

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			// @formatter:off
			Options options = new Options();
			OptionHelper.addL(options, OPTION_INPUT, true, true, "an input style file");
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "an output style file");
			OptionHelper.addL(options, OPTION_MODE, true, true, "mode");
			OptionHelper.addL(options, OPTION_ANGLE, true, false, "roation angle of hue");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws SAXException, IOException, ParserConfigurationException,
			TransformerException
	{
		CommandLine line = arguments.getLine();

		String pathInput = line.getOptionValue(OPTION_INPUT);
		String pathOutput = line.getOptionValue(OPTION_OUTPUT);

		String mode = line.getOptionValue(OPTION_MODE);
		if (!possibleModes.contains(mode)) {
			System.out.println("modification mode not supported: " + mode);
			System.out.println("supported modes:");
			for (String supportedMode : possibleModes) {
				System.out.println("	" + supportedMode);
			}
			System.exit(1);
		}

		float angle = 0;
		boolean needAngle = mode.equals(MODE_ROTATE_HUE);
		if (needAngle) {
			if (!line.hasOption(OPTION_ANGLE)) {
				System.out.println("please specify the angle");
				System.exit(1);
			}
			String value = line.getOptionValue(OPTION_ANGLE);
			try {
				angle = Float.parseFloat(value);
			} catch (NumberFormatException e) {
				System.out.println(
						"unable to parse angle. please specify a valid angle");
				System.exit(1);
			}
		}

		System.out.println("input: " + pathInput);
		System.out.println("output: " + pathOutput);
		System.out.println("mode: " + mode);

		ColorConverter converter = null;
		if (mode.equals(MODE_INVERT_RGB)) {
			converter = new ConverterInvertRGB();
		} else if (mode.equals(MODE_INVERT_LUMINANCE)) {
			converter = new ConverterInvertLuminance();
		} else if (mode.equals(MODE_INVERT_RGB_LUMINANCE)) {
			converter = new ConverterInvertRGBLuminance();
		} else if (mode.equals(MODE_ROTATE_HUE)) {
			converter = new ConverterRotateHue(angle);
		}

		ConfigBundle input;
		try {
			input = ConfigBundleReader.readConfigBundle(new File(pathInput));
		} catch (InvalidBundleException e) {
			System.out.println("unable to convert config: " + e.getMessage());
			return;
		}
		ConfigBundle output = BundleChanger.modify(input, converter);
		ConfigBundleWriter.writeConfigBundle(new FileOutputStream(pathOutput),
				output);
	}

}
