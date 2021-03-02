package de.topobyte.mapocado.mapformat.profiling;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.topobyte.guava.util.MultisetUtil;
import de.topobyte.guava.util.Order;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.tbo.access.TboIterator;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

public class OsmStringProfiler
{

	private static final String OPTION_INPUT = "input";

	public static void main(String[] args) throws IOException
	{
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, true,
				"a file to read osm data from");

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

		String pathInput = line.getOptionValue(OPTION_INPUT);

		FileInputStream fis = new FileInputStream(pathInput);
		BufferedInputStream bis = new BufferedInputStream(fis);
		OsmIterator iterator = new TboIterator(bis, true, false);

		OsmStringProfiler profiler = new OsmStringProfiler();
		profiler.run(iterator);
	}

	private void run(OsmIterator iterator)
	{
		int nEntities = 0;

		Multiset<String> ms = HashMultiset.create();

		while (iterator.hasNext()) {
			EntityContainer container = iterator.next();
			OsmEntity entity = container.getEntity();

			nEntities++;

			Map<String, String> tags = OsmModelUtil.getTagsAsMap(entity);

			for (Entry<String, String> entry : tags.entrySet()) {
				ms.add(entry.getKey());
				ms.add(entry.getValue());
			}
		}

		// print all strings, ascending order of occurences
		for (com.google.common.collect.Multiset.Entry<String> entry : MultisetUtil
				.entries(ms, Order.ASCENDING, Order.ASCENDING)) {
			System.out.println(String.format("%s: %d", entry.getElement(),
					entry.getCount()));
		}

		// measure bytes used for plain string storing
		int bytes = 0;
		for (com.google.common.collect.Multiset.Entry<String> entry : MultisetUtil
				.entries(ms, Order.ASCENDING, Order.ASCENDING)) {
			int occs = entry.getCount();
			String string = entry.getElement();
			bytes += occs * string.length();
		}

		System.out.println("total number of bytes for strings: " + bytes);

		// i'th entry of byteList contains the cumulated number of bytes used
		// for the first i strings occurring
		List<Integer> byteList = new ArrayList<>();
		byteList.add(0);

		int i = 0;
		for (com.google.common.collect.Multiset.Entry<String> entry : MultisetUtil
				.entries(ms, Order.DESCENDING, Order.ASCENDING)) {
			int occs = entry.getCount();
			String string = entry.getElement();
			int bytesTilI = byteList.get(i) + occs * string.length();
			byteList.add(bytesTilI);
			i++;
		}

		for (i = 0; i < 100; i++) {
			int bytesTilI = byteList.get(i);
			double relative = bytesTilI / (double) bytes * 100;
			System.out.println(String.format("until %d: %d bytes (%.2f %%)", i,
					bytesTilI, relative));
		}

		System.out.println("number of entities in file: " + nEntities);
	}

}
