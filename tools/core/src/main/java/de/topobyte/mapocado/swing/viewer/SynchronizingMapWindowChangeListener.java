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
