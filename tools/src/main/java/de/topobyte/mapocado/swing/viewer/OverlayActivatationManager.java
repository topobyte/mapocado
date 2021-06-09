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

import java.util.Timer;
import java.util.TimerTask;

import de.topobyte.jeography.core.mapwindow.MapWindowChangeListener;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.mapocado.swing.rendering.labels.NodePainter;

/**
 * The OverlayActivationManager is an object that implements the
 * MapWindowChangeListener interface and may thus be added to a Viewer's
 * MapWindow instance. The object will deactivate the specified nodePainter as
 * soon as the mapWindows' properties change. After the delay, specified with
 * the timeout parameter, elapsed the nodePainter will be activated again and a
 * repaint of the viewer will be scheduled. If subsequent changes to the
 * mapWindow happen during the delay phase, the reactivation will be scheduled
 * to happen accordingly later such that the delay will be honored for the last
 * change that occurred after all.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class OverlayActivatationManager implements MapWindowChangeListener
{

	private final int timeout;

	private final Viewer viewer;
	private final NodePainter nodePainter;

	private Timer timer;
	private Task task = null;

	/**
	 * Creates a new OverlayActivationManager with the specified parameters.
	 * 
	 * @param viewer
	 *            the viewer to refresh after the timeout elapsed.
	 * @param nodePainter
	 *            the nodePainter to enable / disable depending on the
	 *            MapWindow's activity.
	 * @param timeout
	 *            the timeout in milliseconds.
	 */
	public OverlayActivatationManager(Viewer viewer, NodePainter nodePainter,
			int timeout)
	{
		this.timeout = timeout;
		this.viewer = viewer;
		this.nodePainter = nodePainter;

		timer = new Timer();
	}

	@Override
	public void changed()
	{
		if (task != null) {
			task.cancel();
		}
		nodePainter.setEnabled(false);
		task = new Task();
		timer.schedule(task, timeout);
	}

	private class Task extends TimerTask
	{

		@Override
		public void run()
		{
			nodePainter.setEnabled(true);
			viewer.repaint();
		}

	}
}
