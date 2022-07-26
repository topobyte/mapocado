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

package de.topobyte.jeography.tools.cityviewer;

import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToolBar;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.jeography.tools.cityviewer.action.QuitAction;
import de.topobyte.jeography.tools.cityviewer.action.SearchAction;
import de.topobyte.jeography.tools.cityviewer.theme.Style;
import de.topobyte.jeography.tools.cityviewer.theme.StyleItemListener;
import de.topobyte.jeography.tools.cityviewer.theme.StyleListModel;
import de.topobyte.jeography.viewer.action.ZoomAction;
import de.topobyte.jeography.viewer.core.Viewer;

public class Toolbar extends JToolBar
{

	private static final long serialVersionUID = 6734754388683473265L;

	public Toolbar(final CityViewer cityViewer, List<Style> styles)
	{
		Viewer viewer = cityViewer.getViewer();

		QuitAction quit = new QuitAction();
		SearchAction search = new SearchAction(cityViewer);
		ZoomAction zoomIn = new ZoomAction(viewer, true);
		ZoomAction zoomOut = new ZoomAction(viewer, false);

		JButton buttonQuit = new JButton(quit);
		buttonQuit.setText(null);
		JButton buttonSearch = new JButton(search);

		JButton buttonZoomIn = new JButton(zoomIn);
		buttonZoomIn.setText(null);
		JButton buttonZoomOut = new JButton(zoomOut);
		buttonZoomOut.setText(null);

		StyleListModel styleListModel = new StyleListModel(styles);
		JComboBox<Style> choiceTheme = new JComboBox<>(styleListModel);
		choiceTheme.setSelectedIndex(0);

		StyleItemListener styleItemListener = new StyleItemListener() {
			@Override
			public void setStyle(Style style)
			{
				try {
					cityViewer.setStyle(style);
				} catch (IOException e) {
					System.out.println("unable to set style");
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					System.out.println("unable to set style");
					e.printStackTrace();
				} catch (SAXException e) {
					System.out.println("unable to set style");
					e.printStackTrace();
				}
			}
		};
		choiceTheme.addItemListener(styleItemListener);

		add(buttonQuit);
		add(buttonSearch);
		add(buttonZoomIn);
		add(buttonZoomOut);
		add(choiceTheme);
	}
}
