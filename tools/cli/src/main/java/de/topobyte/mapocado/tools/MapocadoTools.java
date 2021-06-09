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

package de.topobyte.mapocado.tools;

import de.topobyte.jeography.tools.cityviewer.RunCityViewer;
import de.topobyte.jeography.tools.cityviewer.RunStandaloneCityViewer;
import de.topobyte.mapocado.styles.convert.RunBundleChanger;
import de.topobyte.mapocado.styles.convert.RunStyleChanger;
import de.topobyte.mapocado.styles.visualize.html.HtmlCreator;
import de.topobyte.mapocado.swing.viewer.RunMapStyleEditor;
import de.topobyte.mapocado.swing.viewer.RunMapViewerCompare;
import de.topobyte.mapocado.swing.viewer.RunMapViewerNook;
import de.topobyte.mapocado.swing.viewer.RunMapViewerSimple;
import de.topobyte.utilities.apache.commons.cli.commands.ArgumentParser;
import de.topobyte.utilities.apache.commons.cli.commands.ExeRunner;
import de.topobyte.utilities.apache.commons.cli.commands.ExecutionData;
import de.topobyte.utilities.apache.commons.cli.commands.RunnerException;
import de.topobyte.utilities.apache.commons.cli.commands.options.DelegateExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class MapocadoTools
{

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("city-viewer", RunCityViewer.OPTIONS_FACTORY,
					RunCityViewer.class);
			options.addCommand("standalone-city-viewer",
					RunStandaloneCityViewer.class);

			options.addCommand("map-viewer-simple",
					RunMapViewerSimple.OPTIONS_FACTORY,
					RunMapViewerSimple.class);
			options.addCommand("map-viewer-nook",
					RunMapViewerNook.OPTIONS_FACTORY, RunMapViewerNook.class);
			options.addCommand("map-viewer-compare",
					RunMapViewerCompare.OPTIONS_FACTORY,
					RunMapViewerCompare.class);
			options.addCommand("map-style-editor",
					RunMapStyleEditor.OPTIONS_FACTORY, RunMapStyleEditor.class);

			options.addCommand("create-html", HtmlCreator.OPTIONS_FACTORY,
					HtmlCreator.class);

			options.addCommand("style-changer", RunStyleChanger.OPTIONS_FACTORY,
					RunStyleChanger.class);
			options.addCommand("bundle-changer",
					RunBundleChanger.OPTIONS_FACTORY, RunBundleChanger.class);

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
