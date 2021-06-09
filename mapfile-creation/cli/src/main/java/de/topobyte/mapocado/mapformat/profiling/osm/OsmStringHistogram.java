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

package de.topobyte.mapocado.mapformat.profiling.osm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import de.topobyte.guava.util.MultisetUtil;
import de.topobyte.guava.util.Order;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.tbo.access.TboIterator;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class OsmStringHistogram
{

	final static Logger logger = LoggerFactory.getLogger(OsmStringHistogram.class);

	public static void main(String[] args) throws IOException
	{
		Options options = new Options();
		OptionHelper.addL(options, "input", true, true, "file", "osm tbo");
		OptionHelper.addL(options, "key", true, true, "string", "key");

		CommandLine commandLine = null;
		try {
			commandLine = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			System.out
					.println("unable to parse command line: " + e.getMessage());
			System.exit(1);
		}
		if (commandLine == null) {
			return;
		}

		String pathInput = commandLine.getOptionValue("input");
		String filterKey = commandLine.getOptionValue("key");

		FileInputStream fis = new FileInputStream(pathInput);
		BufferedInputStream bis = new BufferedInputStream(fis);
		OsmIterator iterator = new TboIterator(bis, true, false);

		int nEntities = 0;

		Multiset<String> counter = HashMultiset.create();

		while (iterator.hasNext()) {
			EntityContainer container = iterator.next();
			OsmEntity entity = container.getEntity();
			nEntities++;

			Map<String, String> tags = OsmModelUtil.getTagsAsMap(entity);

			for (String key : tags.keySet()) {
				if (key.equals(filterKey)) {
					String value = tags.get(key);
					counter.add(value);
				}
			}
		}

		List<Entry<String>> histogram = MultisetUtil.entries(counter,
				Order.ASCENDING, Order.ASCENDING);
		for (Entry<String> entry : histogram) {
			System.out.println(String.format("%s: %d", entry.getElement(),
					entry.getCount()));
		}

		System.out.println("number of entities in file: " + nEntities);
	}

}
