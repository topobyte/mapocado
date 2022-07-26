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

package de.topobyte.mapocado.styles.visualize.html;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.styles.bundled.BundleExtractor;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.ConfigBundleReader;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;
import de.topobyte.mapocado.styles.classes.ClassFileHandler;
import de.topobyte.mapocado.styles.classes.ClassFileReader;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.rules.RuleFileReader;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;
import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class HtmlCreator
{
	static final Logger logger = LoggerFactory.getLogger(HtmlCreator.class);

	private static final String OPTION_CONFIG_RULES = "rules";
	private static final String OPTION_CONFIG_STYLE = "style";
	private static final String OPTION_OUTPUT = "output";

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			// @formatter:off
			Options options = new Options();
			OptionHelper.addL(options, OPTION_OUTPUT, true, true, "a html file to create");
			OptionHelper.addL(options, OPTION_CONFIG_RULES, true, true, "a render-rule config");
			OptionHelper.addL(options, OPTION_CONFIG_STYLE, true, true, "a render-style package");
			// @formatter:on
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	/**
	 * @param args
	 *            see help
	 */
	public static void main(String name, CommonsCliArguments arguments)
	{
		CommandLine line = arguments.getLine();

		String outputDirectory = line.getOptionValue(OPTION_OUTPUT);
		String configFileRules = line.getOptionValue(OPTION_CONFIG_RULES);
		String fileStyle = line.getOptionValue(OPTION_CONFIG_STYLE);

		RuleSet rules = new RuleSet();
		try {
			File[] ruleFiles = new File(configFileRules).listFiles();
			for (File ruleFile : ruleFiles) {
				if (!ruleFile.getName().endsWith(".xml")) {
					continue;
				}
				RuleFileReader.read(rules, ruleFile.getPath());
			}
		} catch (Exception e) {
			logger.error("Exception while parsing rules: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		ConfigBundle configBundle = null;
		try {
			configBundle = ConfigBundleReader
					.readConfigBundle(new File(fileStyle));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} catch (InvalidBundleException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		ClassFileHandler classFileHandler = null;
		try {
			classFileHandler = ClassFileReader
					.read(configBundle.getClassesAsInputStream());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

		File dir = new File(outputDirectory);
		if (dir.exists() && !dir.isDirectory()) {
			System.out.println("output file exists, but is not a directory");
			System.exit(1);
		}
		if (dir.exists() && !dir.canWrite()) {
			System.out.println("output directory exists, but is not writable");
			System.exit(1);
		}
		if (!dir.exists()) {
			boolean success = dir.mkdirs();
			if (!success) {
				System.out.println("unable to create output directory");
				System.exit(1);
			}
		}
		if (dir.exists() && !dir.canWrite()) {
			System.out.println("output directory exists, but is not writable");
			System.exit(1);
		}

		HtmlCreator htmlCreator = new HtmlCreator(dir, rules, configBundle,
				classFileHandler);

		try {
			htmlCreator.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private final File outputDirectory;
	private final RuleSet rulSet;
	private final ClassFileHandler classFileHandler;
	private final ConfigBundle configBundle;

	public HtmlCreator(File outputDirectory, RuleSet ruleSet,
			ConfigBundle configBundle, ClassFileHandler classFileHandler)
	{
		this.rulSet = ruleSet;
		this.configBundle = configBundle;
		this.classFileHandler = classFileHandler;
		this.outputDirectory = outputDirectory;
	}

	private void execute() throws IOException
	{
		BundleExtractor extractor = new BundleExtractor(outputDirectory,
				configBundle);
		extractor.extract();

		File fileClasses = new File(outputDirectory, "classes.html");
		ClassFileCreator classFileCreator = new ClassFileCreator(
				classFileHandler.getObjectClasses());
		classFileCreator.createClassesFile(fileClasses);

		Map<String, ObjectClass> refToClass = new HashMap<>();
		for (ObjectClass objectClass : classFileHandler.getObjectClasses()) {
			String id = objectClass.getId();
			refToClass.put(id, objectClass);
		}

		File fileRules = new File(outputDirectory, "rules.html");
		RuleFileCreator ruleFileCreator = new RuleFileCreator(rulSet,
				refToClass);
		ruleFileCreator.createRulesFile(fileRules);
	}
}
