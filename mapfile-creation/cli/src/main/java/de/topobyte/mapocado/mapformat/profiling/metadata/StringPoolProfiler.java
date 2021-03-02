package de.topobyte.mapocado.mapformat.profiling.metadata;

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
import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;

import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DefaultTraversalHandler;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class StringPoolProfiler
{

	static final Logger logger = LoggerFactory
			.getLogger(StringPoolProfiler.class);

	private static final String OPTION_INPUT = "input";

	public static void main(String[] args) throws IOException
	{
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, true, "an input file");

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

		StringPoolProfiler profiler = new StringPoolProfiler(mapfile);
		profiler.profile();
	}

	private final Mapfile mapfile;

	public StringPoolProfiler(Mapfile mapfile)
	{
		this.mapfile = mapfile;
	}

	private Multiset<Integer> counter = HashMultiset.create();

	private void profile() throws IOException
	{
		StringPool poolForRefs = mapfile.getMetadata().getPoolForRefs();
		int n = poolForRefs.getNumberOfEntries();

		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();
		IntervalTree<Integer, DiskTree<Node>> treeNodes = mapfile
				.getTreeNodes();

		List<Integer> intervalsWays = treeWays.getIntervalStarts();
		List<Integer> intervalsNodes = treeNodes.getIntervalStarts();

		for (int i = -1; i < intervalsWays.size(); i++) {
			int zoom = i < 0 ? 0 : intervalsWays.get(i);
			System.out.println("zoom: " + zoom);
			DiskTree<Way> tree = treeWays.getObject(zoom);
			EntityTraversalHandler handler = new EntityTraversalHandler();
			tree.traverse(handler);
		}

		for (int i = -1; i < intervalsNodes.size(); i++) {
			int zoom = i < 0 ? 0 : intervalsNodes.get(i);
			System.out.println("zoom: " + zoom);
			DiskTree<Node> tree = treeNodes.getObject(zoom);
			EntityTraversalHandler handler = new EntityTraversalHandler();
			tree.traverse(handler);
		}

		for (int i = 0; i < n; i++) {
			String string = poolForRefs.getString(i);
			int occs = counter.count(i);
			System.out.println(String.format(
					"class #%d: name: %s, occurences: %d", i, string, occs));
		}
	}

	private class EntityTraversalHandler extends DefaultTraversalHandler<Entity>
	{

		@Override
		public void handleEntry(Entry entry, Entity thing, int level)
		{
			TIntArrayList classes = thing.getClasses();
			for (int c : classes.toArray()) {
				counter.add(c);
			}
		}
	}

}
