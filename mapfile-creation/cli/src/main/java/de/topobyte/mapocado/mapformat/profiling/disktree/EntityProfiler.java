package de.topobyte.mapocado.mapformat.profiling.disktree;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.guava.util.MultisetUtil;
import de.topobyte.guava.util.Order;
import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DefaultTraversalHandler;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class EntityProfiler
{

	static final Logger logger = LoggerFactory.getLogger(EntityProfiler.class);

	private static final String OPTION_INPUT = "input";

	public static void main(String[] args) throws IOException
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
			e.printStackTrace();
			System.exit(1);
		}

		EntityProfiler profiler = new EntityProfiler(mapfile);
		profiler.profile();
	}

	private final Mapfile mapfile;

	public EntityProfiler(Mapfile mapfile)
	{
		this.mapfile = mapfile;
	}

	private void profile() throws IOException
	{
		System.out.println("PROFILING WAYS");
		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();
		List<Integer> intervals = treeWays.getIntervalStarts();
		for (int i = -1; i < intervals.size(); i++) {
			int zoom = i < 0 ? 0 : intervals.get(i);
			System.out.println("PROFILING TREE. zoom: " + zoom);
			DiskTree<Way> tree = treeWays.getObject(zoom);
			EntityTraversalHandler handler = new EntityTraversalHandler();
			tree.traverse(handler);
			print(handler);
		}

		System.out.println("PROFILING NODES");
		IntervalTree<Integer, DiskTree<Node>> treeNodes = mapfile
				.getTreeNodes();
		intervals = treeNodes.getIntervalStarts();
		for (int i = -1; i < intervals.size(); i++) {
			int zoom = i < 0 ? 0 : intervals.get(i);
			System.out.println("PROFILING TREE. zoom: " + zoom);
			DiskTree<Node> tree = treeNodes.getObject(zoom);
			EntityTraversalHandler handler = new EntityTraversalHandler();
			tree.traverse(handler);
			print(handler);
		}
	}

	private void print(EntityTraversalHandler handler)
	{
		System.out.println("Tags");
		for (Entry<Integer> entry : MultisetUtil.entries(handler.tagCounter,
				Order.ASCENDING, Order.ASCENDING)) {
			System.out.println(String.format("# %d: %d", entry.getCount(),
					entry.getElement()));
		}

		System.out.println("Classes");
		for (Entry<Integer> entry : MultisetUtil.entries(handler.classCounter,
				Order.ASCENDING, Order.ASCENDING)) {
			System.out.println(String.format("# %d: %d", entry.getCount(),
					entry.getElement()));
		}
	}

	private class EntityTraversalHandler extends DefaultTraversalHandler<Entity>
	{
		Multiset<Integer> classCounter = HashMultiset.create();
		Multiset<Integer> tagCounter = HashMultiset.create();

		@Override
		public void handleEntry(
				de.topobyte.mapocado.mapformat.rtree.disk.Entry entry,
				Entity thing, int level)
		{
			TIntObjectHashMap<String> tags = thing.getTags();
			tagCounter.add(tags.size());
			TIntArrayList classes = thing.getClasses();
			classCounter.add(classes.size());
		}
	}

}
