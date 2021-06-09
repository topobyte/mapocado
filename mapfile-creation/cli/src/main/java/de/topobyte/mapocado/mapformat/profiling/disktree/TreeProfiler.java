package de.topobyte.mapocado.mapformat.profiling.disktree;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.model.Byteable;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.mapformat.rtree.disk.TraversalHandler;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class TreeProfiler
{

	static final Logger logger = LoggerFactory.getLogger(TreeProfiler.class);

	private static final String OPTION_INPUT = "input";

	public static void main(String name, String[] args) throws IOException
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

		TreeProfiler profiler = new TreeProfiler(mapfile);
		profiler.profile();
	}

	private final Mapfile mapfile;
	private final Metadata metadata;

	public TreeProfiler(Mapfile mapfile)
	{
		this.mapfile = mapfile;
		this.metadata = mapfile.getMetadata();
	}

	private void profile() throws IOException
	{
		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();

		List<Integer> intervals = treeWays.getIntervalStarts();
		for (int i = -1; i < intervals.size(); i++) {
			int zoom = i < 0 ? 0 : intervals.get(i);
			System.out.println("PROFILING TREE. zoom: " + zoom);
			DiskTree<Way> tree = treeWays.getObject(zoom);
			WayTraversalHandler handler = new WayTraversalHandler();
			tree.traverse(handler);
			print(handler);
		}
	}

	private void print(WayTraversalHandler handler)
	{
		// @formatter:off
		System.out.println("number of inner nodes: " + handler.numberOfInnerNodes);
		System.out.println("number of inner node bytes: " + handler.numberOfInnerNodeBytes);
		
		System.out.println("number of leafs: " + handler.numberOfLeafs);
		
		System.out.println("number of elements: " + handler.numberOfElements);
		System.out.println("number of element bytes: " + handler.numberOfElementBytes);
		
		System.out.println("number of way strings: " + handler.numberOfStrings);
		System.out.println("number of way string chars: " + handler.numberOfStringChars);
		System.out.println("number of way string bytes: " + handler.numberOfStringBytes);
		// @formatter:on
	}

	private class WayTraversalHandler implements TraversalHandler<Way>
	{
		private int numberOfInnerNodes = 0;
		private int numberOfInnerNodeBytes = 0;

		private int numberOfLeafs = 0;

		private int numberOfElements = 0;
		private int numberOfElementBytes = 0;

		private int numberOfStrings = 0;
		private int numberOfStringBytes = 0;
		private int numberOfStringChars = 0;

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
				Way thing, int level)
		{
			handleEntry(entry, thing);
			TIntObjectHashMap<String> tags = thing.getTags();
			for (int key : tags.keys()) {
				String value = tags.get(key);
				numberOfStrings += 1;
				numberOfStringChars += value.length();
				numberOfStringBytes += value.getBytes().length;
			}
		}
	}

}
