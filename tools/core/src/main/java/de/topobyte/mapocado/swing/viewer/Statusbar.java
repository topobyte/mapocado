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

package de.topobyte.mapocado.swing.viewer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import de.topobyte.jeography.core.mapwindow.MapWindow;
import de.topobyte.jeography.core.mapwindow.MapWindowChangeListener;
import de.topobyte.jeography.viewer.core.Viewer;

public class Statusbar extends JPanel implements MapWindowChangeListener
{
	private static final long serialVersionUID = -930099350921773862L;

	private Viewer viewer;
	private MapWindow mapWindow;

	private JLabel label = new JLabel();

	public Statusbar(Viewer viewer)
	{
		this.viewer = viewer;
		mapWindow = viewer.getMapWindow();
		mapWindow.addChangeListener(Statusbar.this);
		add(label);
	}

	@Override
	public void changed()
	{
		String text = String.format(
				"size: %d x %d, zoom: %.1f, lat: %f, lon: %f",
				viewer.getWidth(), viewer.getHeight(), mapWindow.getZoom(),
				mapWindow.getCenterLat(), mapWindow.getCenterLon());
		label.setText(text);
	}

}