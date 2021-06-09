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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.guava.util.MultisetUtil;
import de.topobyte.guava.util.Order;
import de.topobyte.mapocado.mapformat.MapfileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.model.Byteable;
import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.mapformat.rtree.disk.TraversalHandler;
import de.topobyte.mapocado.styles.classes.ClassFileHandler;
import de.topobyte.mapocado.styles.classes.ClassFileReader;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.rules.Rule;
import de.topobyte.mapocado.styles.rules.RuleFileHelper;
import de.topobyte.mapocado.styles.rules.RuleFileReader;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class TreeProfilerClasses
{

	static final Logger logger = LoggerFactory
			.getLogger(TreeProfilerClasses.class);

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_RULES = "rules";
	private static final String OPTION_CLASSES = "classes";

	public static void main(String name, String[] args)
			throws IOException, ParserConfigurationException, SAXException
	{
		// @formatter:off
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, true, "an rtree file");
		OptionHelper.addL(options, OPTION_RULES, true, true, "a rules xml directory");
		OptionHelper.addL(options, OPTION_CLASSES, true, true, "a classes xml file");
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

		String configDir = line.getOptionValue(OPTION_RULES);
		String classesFile = line.getOptionValue(OPTION_CLASSES);
		String inputFile = line.getOptionValue(OPTION_INPUT);

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

		Map<String, List<List<Rule>>> classRefToRules = RuleFileHelper
				.buildClassToRules(config.getRules());

		Mapfile mapfile = null;
		try {
			mapfile = MapfileAccess.open(new File(inputFile));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Map<String, ObjectClass> classLookup = new HashMap<>();
		ClassFileHandler classFileHandler = ClassFileReader.read(classesFile);
		for (ObjectClass oc : classFileHandler.getObjectClasses()) {
			classLookup.put(oc.getId(), oc);
		}

		TreeProfilerClasses profiler = new TreeProfilerClasses(mapfile,
				classRefToRules, classLookup);
		profiler.profile();
	}

	private final Mapfile mapfile;
	private final Metadata metadata;

	private final Map<String, List<List<Rule>>> classRefToRules;
	private final Map<String, ObjectClass> classLookup;

	public TreeProfilerClasses(Mapfile mapfile,
			Map<String, List<List<Rule>>> classRefToRules,
			Map<String, ObjectClass> classLookup)
	{
		this.mapfile = mapfile;
		this.metadata = mapfile.getMetadata();
		this.classRefToRules = classRefToRules;
		this.classLookup = classLookup;
	}

	private void profile() throws IOException
	{
		IntervalTree<Integer, DiskTree<Node>> treeNodes = mapfile
				.getTreeNodes();
		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();
		IntervalTree<Integer, DiskTree<Relation>> treeRelations = mapfile
				.getTreeRelations();

		List<Integer> intervalsNodes = treeNodes.getIntervalStarts();
		List<Integer> intervalsWays = treeWays.getIntervalStarts();
		List<Integer> intervalsRelations = treeRelations.getIntervalStarts();

		for (int i = -1; i < intervalsNodes.size(); i++) {
			int zoom = i < 0 ? 0 : intervalsNodes.get(i);
			System.out.println();
			System.out.println("PROFILING NODE TREE. starting zoom: " + zoom);
			DiskTree<Node> tree = treeNodes.getObject(zoom);
			EntityTraversalHandler<Node> handler = new EntityTraversalHandler<>();
			tree.traverse(handler);
			print(handler);
		}
		for (int i = -1; i < intervalsWays.size(); i++) {
			int zoom = i < 0 ? 0 : intervalsWays.get(i);
			System.out.println();
			System.out.println("PROFILING WAY TREE. starting zoom: " + zoom);
			DiskTree<Way> tree = treeWays.getObject(zoom);
			EntityTraversalHandler<Way> handler = new EntityTraversalHandler<>();
			tree.traverse(handler);
			print(handler);
		}
		for (int i = -1; i < intervalsRelations.size(); i++) {
			int zoom = i < 0 ? 0 : intervalsRelations.get(i);
			System.out.println();
			System.out
					.println("PROFILING RELATION TREE. starting zoom: " + zoom);
			DiskTree<Relation> tree = treeRelations.getObject(zoom);
			EntityTraversalHandler<Relation> handler = new EntityTraversalHandler<>();
			tree.traverse(handler);
			print(handler);
		}
	}

	private void print(EntityTraversalHandler<? extends Entity> handler)
	{
		// @formatter:off
		System.out.println("number of inner nodes: " + handler.numberOfInnerNodes);
		System.out.println("number of inner node bytes: " + handler.numberOfInnerNodeBytes);
		
		System.out.println("number of leafs: " + handler.numberOfLeafs);
		
		System.out.println("number of elements: " + handler.numberOfElements);
		System.out.println("number of element bytes: " + handler.numberOfElementBytes);
		
		System.out.println("number of strings: " + handler.numberOfStrings);
		System.out.println("number of string chars: " + handler.numberOfStringChars);
		System.out.println("number of string bytes: " + handler.numberOfStringBytes);
		// @formatter:on

		for (Entry<Integer> entry : MultisetUtil.entries(
				handler.classRefCounter, Order.ASCENDING, Order.ASCENDING)) {
			int refId = entry.getElement();
			int count = handler.classRefCounter.count(refId);
			String ref = metadata.getPoolForRefs().getString(refId);
			List<List<Rule>> rules = classRefToRules.get(ref);
			ObjectClass objectClass = classLookup.get(ref);
			List<String> renderElementNames = getRenderElementNames(
					objectClass.elements);
			System.out.println(String.format("%d: %s %s -> %s", count, ref,
					renderElementNames, rules));
		}
	}

	private List<String> getRenderElementNames(RenderElement[] renderElements)
	{
		List<String> names = new ArrayList<>();
		for (RenderElement element : renderElements) {
			names.add(element.getClass().getSimpleName());
		}
		return names;
	}

	private class EntityTraversalHandler<T extends Entity>
			implements TraversalHandler<T>
	{
		private int numberOfInnerNodes = 0;
		private int numberOfInnerNodeBytes = 0;

		private int numberOfLeafs = 0;

		private int numberOfElements = 0;
		private int numberOfElementBytes = 0;

		private int numberOfStrings = 0;
		private int numberOfStringBytes = 0;
		private int numberOfStringChars = 0;

		private Multiset<Integer> classRefCounter = HashMultiset.create();

		@Override
		public void handleInnerNode(
				de.topobyte.mapocado.mapformat.rtree.disk.Entry entry,
				de.topobyte.mapocado.mapformat.rtree.disk.Node node, int level)
		{
			numberOfInnerNodes += 1;
			numberOfInnerNodeBytes += 2;
			numberOfInnerNodeBytes += 20 * node.getEntries().size();
		}

		@Override
		public void handleLeaf(
				de.topobyte.mapocado.mapformat.rtree.disk.Entry entry,
				de.topobyte.mapocado.mapformat.rtree.disk.Node leaf, int level)
		{
			numberOfLeafs += 1;
			numberOfElements += leaf.getEntries().size();
		}

		public void handleEntry(
				de.topobyte.mapocado.mapformat.rtree.disk.Entry entry,
				Byteable byteable)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				byteable.write(baos, entry, metadata);
			} catch (IOException e) {
				e.printStackTrace();
			}
			numberOfElementBytes += baos.size();
		}

		@Override
		public void handleEntry(
				de.topobyte.mapocado.mapformat.rtree.disk.Entry entry,
				Entity thing, int level)
		{
			handleEntry(entry, thing);
			TIntObjectHashMap<String> tags = thing.getTags();
			for (int key : tags.keys()) {
				String value = tags.get(key);
				numberOfStrings += 1;
				numberOfStringChars += value.length();
				numberOfStringBytes += value.getBytes().length;
			}

			// Set<LightObjectClassRef> classes = thing.getClasses();
			for (int refId : thing.getClasses().toArray()) {
				// for (LightObjectClassRef ref : classes) {
				// classRefCounter.put(ref.getRef());
				classRefCounter.add(refId);
			}
		}
	}

}
