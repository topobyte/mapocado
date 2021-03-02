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