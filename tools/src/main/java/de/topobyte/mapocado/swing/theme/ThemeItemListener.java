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

package de.topobyte.mapocado.swing.theme;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.mapocado.swing.viewer.MapocadoViewer;

public class ThemeItemListener implements ItemListener
{

	final static Logger logger = LoggerFactory
			.getLogger(ThemeItemListener.class);

	private final MapocadoViewer viewer;

	public ThemeItemListener(MapocadoViewer viewer)
	{
		this.viewer = viewer;
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		Theme theme = (Theme) e.getItem();
		if (e.getStateChange() == ItemEvent.SELECTED) {
			System.out.println(theme);
			setTheme(theme);
		}
	}

	private void setTheme(Theme theme)
	{
		try {
			viewer.setStyleFromTheme(theme);
		} catch (IOException e) {
			System.out.println("unable to set theme: " + e.getMessage());
		} catch (ParserConfigurationException e) {
			System.out.println("unable to set theme: " + e.getMessage());
		} catch (SAXException e) {
			System.out.println("unable to set theme: " + e.getMessage());
		}
	}

}
