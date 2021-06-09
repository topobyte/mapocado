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

import java.util.ArrayList;
import java.util.List;

import de.topobyte.jeography.core.mapwindow.MapWindowChangeListener;
import de.topobyte.jeography.viewer.core.Viewer;

public class SynchronizingMapWindowChangeListener
		implements MapWindowChangeListener
{

	private final Viewer master;
	private List<Viewer> slaves = new ArrayList<>();

	public SynchronizingMapWindowChangeListener(Viewer master,
			Viewer... viewers)
	{
		this.master = master;
		for (Viewer viewer : viewers) {
			slaves.add(viewer);
		}
		master.getMapWindow().addChangeListener(this);
	}

	// private double lastLon = 0;
	// private double lastLat = 0;

	@Override
	public void changed()
	{
		double lon = master.getMapWindow().getCenterLon();
		double lat = master.getMapWindow().getCenterLat();
		int zoom = master.getMapWindow().getZoomLevel();

		// if (lon == lastLon && lat == lastLat) {
		// return;
		// }
		// lastLon = lon;
		// lastLat = lat;

		for (Viewer slave : slaves) {
			slave.getMapWindow().gotoLonLat(lon, lat);
			slave.getMapWindow().zoom(zoom);
			slave.repaint();
		}
	}

}
