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

package de.topobyte.jeography.tools.cityviewer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.xml.sax.SAXException;

import de.topobyte.jeography.tools.cityviewer.theme.Style;
import de.topobyte.jeography.tools.cityviewer.theme.StyleConfig;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunCityViewer
{

	private static final int STARTUP_ZOOM = 14;

	private static final String OPTION_MAPFILE = "mapfile";
	private static final String OPTION_DATABASE = "database";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			// @formatter:off
			Options options = new Options();
			OptionHelper.addL(options, OPTION_DATABASE, true, true, "file", "search database");
			OptionHelper.addL(options, OPTION_MAPFILE, true, true, "file", "map file");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
	{
		CommandLine line = arguments.getLine();

		if (line == null) {
			return;
		}

		String pathMapfile = line.getOptionValue(OPTION_MAPFILE);
		String pathDatabase = line.getOptionValue(OPTION_DATABASE);

		File fileMapfile = new File(pathMapfile);
		File fileDatabase = new File(pathDatabase);

		Charset defaultCharset = Charset.defaultCharset();
		System.out.println("default charset: " + defaultCharset);

		System.out.println("CityViewer started");
		URL resStyles = Thread.currentThread().getContextClassLoader()
				.getResource(CityViewer.RES_STYLES);

		System.out.println("Mapfile: " + pathMapfile);
		System.out.println("Database: " + pathDatabase);
		System.out.println("Styles: " + resStyles);

		StyleConfig styleConfig = null;
		try {
			styleConfig = StyleConfig.parse(resStyles);
		} catch (IOException | ParserConfigurationException | SAXException e) {
			System.out.println("unable to parse styles.xml");
			e.printStackTrace();
			System.exit(1);
		}
		List<Style> styles = styleConfig.getStyles();

		ConfigBundle configBundle = null;
		Style style = styles.get(0);
		try {
			configBundle = CityViewer.getConfigBundleForStyle(style);
		} catch (IOException | InvalidBundleException e) {
			System.out.println("unable to open style");
			e.printStackTrace();
			System.exit(1);
		}
		try {
			new CityViewer(fileMapfile, configBundle, fileDatabase, false,
					false, false, 0, 0, true, STARTUP_ZOOM, styles);
		} catch (IOException | ClassNotFoundException
				| ParserConfigurationException | SAXException e) {
			System.out.println("unable to lauch viewer");
			e.printStackTrace();
			System.exit(1);
		}
	}

}
