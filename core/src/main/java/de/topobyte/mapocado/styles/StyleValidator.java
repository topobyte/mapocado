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

package de.topobyte.mapocado.styles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.mapocado.styles.classes.element.Area;
import de.topobyte.mapocado.styles.classes.element.LineSymbol;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.Symbol;
import de.topobyte.mapocado.styles.directory.StyleDirectory;
import de.topobyte.mapocado.styles.rules.Rule;
import de.topobyte.mapocado.styles.rules.RuleFileReader;
import de.topobyte.mapocado.styles.rules.RuleSet;

public class StyleValidator
{

	public static void main(String[] args)
	{
		if (args.length < 2) {
			System.out.println(
					"usage: StyleValidator <rulefile> [... <rulefiles>] <style directory>");
			System.exit(1);
		}

		List<String> pathsRulefiles = new ArrayList<>();
		for (int i = 0; i < args.length - 1; i++) {
			pathsRulefiles.add(args[i]);
		}
		String pathStyle = args[args.length - 1];

		File dirStyle = new File(pathStyle);
		StyleDirectory styleDirectory = new StyleDirectory(dirStyle);

		try {
			styleDirectory.init();
		} catch (Exception e) {
			System.out.println(
					"error while reading style directory '" + pathStyle);
			e.printStackTrace();
			System.exit(1);
		}

		RuleSet config = new RuleSet();
		for (String pathRuleFile : pathsRulefiles) {
			try {
				RuleFileReader.read(config, pathRuleFile);
			} catch (Exception e) {
				System.out.println(
						"error while reading rule file '" + pathRuleFile);
				e.printStackTrace();
				System.exit(1);
			}
		}

		/*
		 * start checking
		 */

		StyleValidator styleValidator = new StyleValidator(config,
				styleDirectory);
		styleValidator.run();
	}

	private RuleSet config;
	private StyleDirectory styleDirectory;

	private Map<String, ObjectClass> lookup = new HashMap<>();
	private Set<String> classNames = new HashSet<>();

	private int totalRules = 0;

	private Set<String> patterns;
	private Set<String> symbols;

	private Set<String> usedPatterns;
	private Set<String> usedSymbols;

	private Set<String> missingPatterns;
	private Set<String> missingSymbols;

	public StyleValidator(RuleSet config, StyleDirectory styleDirectory)
	{
		this.config = config;
		this.styleDirectory = styleDirectory;
	}

	private void run()
	{
		getSymbolsAndPatternsFilenames();

		usedPatterns = new HashSet<>();
		usedSymbols = new HashSet<>();

		missingPatterns = new HashSet<>();
		missingSymbols = new HashSet<>();

		removeUsedSymbolsAndPatterns();

		checkForNonUsedRulesAndClasses();

		System.out
				.println("number of non-used class ids: " + classNames.size());
		System.out.println("total number of rules found: " + totalRules);

		printNonUsedSymbols();

		printNonUsedPatterns();

		printMissingSymbols();

		printMissingPatterns();
	}

	private Set<String> getFilenames(File dir)
	{
		Set<String> filenames = new HashSet<>();
		File[] files = dir.listFiles();
		for (File file : files) {
			filenames.add(file.getName());
		}
		return filenames;
	}

	private void getSymbolsAndPatternsFilenames()
	{
		File patternsDir = styleDirectory.getPatternsDir();
		File symbolsDir = styleDirectory.getSymbolsDir();

		patterns = getFilenames(patternsDir);
		symbols = getFilenames(symbolsDir);
	}

	private void removeUsedSymbolsAndPatterns()
	{
		for (ObjectClass objectClass : styleDirectory.getObjectClasses()) {
			String id = objectClass.getId();
			lookup.put(id, objectClass);
			classNames.add(id);

			for (RenderElement re : objectClass.elements) {
				if (re instanceof Symbol) {
					Symbol symbol = (Symbol) re;
					String source = symbol.getSource();
					if (symbols.contains(source)) {
						usedSymbols.add(source);
					} else {
						missingSymbols.add(source);
					}
				} else if (re instanceof LineSymbol) {
					LineSymbol line = (LineSymbol) re;
					String source = line.getSource();
					if (symbols.remove(source)) {
						usedSymbols.add(source);
					} else {
						missingSymbols.add(source);
					}
				} else if (re instanceof Area) {
					Area area = (Area) re;
					String source = area.getSource();
					if (source == null) {
						continue;
					}
					if (patterns.contains(source)) {
						usedPatterns.add(source);
					} else {
						missingPatterns.add(source);
					}
				}
			}
		}
		symbols.removeAll(usedSymbols);
		patterns.removeAll(usedPatterns);
	}

	private void checkForNonUsedRulesAndClasses()
	{
		for (Rule rule : config.getRules()) {
			checkRule(rule);
		}

		for (String classId : classNames) {
			System.out.println("non used class id: " + classId);
		}
	}

	private void checkRule(Rule rule)
	{
		checkSingleRule(rule);
		for (Rule child : rule.getRules()) {
			checkRule(child);
		}
	}

	private void checkSingleRule(Rule rule)
	{
		List<ObjectClassRef> refs = rule.getClasses();
		if (refs.size() != 0) {
			totalRules += 1;
		}
		for (ObjectClassRef ref : refs) {
			ObjectClass objectClass = lookup.get(ref.getRef());
			if (objectClass == null) {
				System.out.println("no class for rule: " + ref.getRef());
			} else {
				classNames.remove(ref.getRef());
			}
		}
	}

	private void printNonUsedSymbols()
	{
		if (symbols.isEmpty()) {
			System.out.println("No unused symbols");
			return;
		}
		System.out.println("Unused symbols:");
		for (String source : symbols) {
			System.out.println(source);
		}
	}

	private void printNonUsedPatterns()
	{
		if (patterns.isEmpty()) {
			System.out.println("No unused patterns");
			return;
		}
		System.out.println("Unused patterns:");
		for (String source : patterns) {
			System.out.println(source);
		}
	}

	private void printMissingSymbols()
	{
		if (missingSymbols.isEmpty()) {
			System.out.println("No missing symbols");
			return;
		}
		System.out.println("Missing symbols");
		for (String source : missingSymbols) {
			System.out.println(source);
		}
	}

	private void printMissingPatterns()
	{
		if (missingPatterns.isEmpty()) {
			System.out.println("No missing patterns");
			return;
		}
		System.out.println("Missing patterns");
		for (String source : missingPatterns) {
			System.out.println(source);
		}
	}
}
