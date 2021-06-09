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

package de.topobyte.mapocado.mapformat;

import de.topobyte.mapocado.mapformat.mapfile.PrintIntervals;
import de.topobyte.mapocado.mapformat.mapfile.PrintMetadata;
import de.topobyte.mapocado.mapformat.profiling.ClassHistogramTest;
import de.topobyte.mapocado.mapformat.profiling.disktree.EntityProfiler;
import de.topobyte.mapocado.mapformat.profiling.disktree.RelationProfiler;
import de.topobyte.mapocado.mapformat.profiling.disktree.StringHistogram;
import de.topobyte.mapocado.mapformat.profiling.disktree.StringPoolProfiler;
import de.topobyte.mapocado.mapformat.profiling.disktree.StringPrinter;
import de.topobyte.mapocado.mapformat.profiling.disktree.TreeProfiler;
import de.topobyte.mapocado.mapformat.profiling.disktree.TreeProfilerClasses;
import de.topobyte.mapocado.mapformat.profiling.disktree.TreeProfilerNonClosedAreas;
import de.topobyte.mapocado.mapformat.profiling.osm.OsmStringProfiler;
import de.topobyte.utilities.apache.commons.cli.commands.ArgumentParser;
import de.topobyte.utilities.apache.commons.cli.commands.ExeRunner;
import de.topobyte.utilities.apache.commons.cli.commands.ExecutionData;
import de.topobyte.utilities.apache.commons.cli.commands.RunnerException;
import de.topobyte.utilities.apache.commons.cli.commands.options.DelegateExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class MapocadoMapfileCli
{

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("mapfile", OPTIONS_FACTORY_MAPFILE);
			options.addCommand("profiling", OPTIONS_FACTORY_PROFILING);

			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_MAPFILE = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("print-intervals", PrintIntervals.class);
			options.addCommand("print-metadata", PrintMetadata.class);

			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_PROFILING = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("disk-tree", OPTIONS_FACTORY_DISK_TREE);
			options.addCommand("osm", OPTIONS_FACTORY_OSM);
			options.addCommand("class-histogram-test",
					ClassHistogramTest.class);

			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_DISK_TREE = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("entity-profiler", EntityProfiler.class);
			options.addCommand("relation-profiler", RelationProfiler.class);
			options.addCommand("string-histogram", StringHistogram.class);
			options.addCommand("string-pool-profiler",
					StringPoolProfiler.class);
			options.addCommand("string-printer", StringPrinter.class);
			options.addCommand("tree-profiler", TreeProfiler.class);
			options.addCommand("tree-profiler-classes",
					TreeProfilerClasses.class);
			options.addCommand("tree-profiler-non-closed-areas",
					TreeProfilerNonClosedAreas.class);

			return options;
		}

	};

	public static ExeOptionsFactory OPTIONS_FACTORY_OSM = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("string-profiler", OsmStringProfiler.class);
			options.addCommand("string-histogram", OsmStringProfiler.class);

			return options;
		}

	};

	public static void main(String[] args) throws RunnerException
	{
		ExeOptions options = OPTIONS_FACTORY.createOptions();
		ArgumentParser parser = new ArgumentParser("mapocado-tools", options);

		ExecutionData data = parser.parse(args);
		if (data != null) {
			ExeRunner.run(data);
		}
	}

}
