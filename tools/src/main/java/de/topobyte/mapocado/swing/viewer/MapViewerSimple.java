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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.mapocado.mapformat.MapfileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.swing.rendering.TreeLeafTileConfig;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapViewerSimple extends JPanel
{

	private static final long serialVersionUID = -7168502470969119340L;

	static final Logger logger = LoggerFactory.getLogger(MapViewerSimple.class);

	private int tileSize;

	private boolean hasStartupPosition = false;
	private double startupLon = 0.0;
	private double startupLat = 0.0;
	private boolean hasStartupZoom = false;
	private int startupZoom = 0;

	private MapocadoViewer viewer;
	private GpsSimulatorOverlay gpsSimulator;

	public MapViewerSimple(int tileSize)
	{
		this.tileSize = tileSize;
	}

	void setStartPosition(double lon, double lat)
	{
		hasStartupPosition = true;
		startupLon = lon;
		startupLat = lat;
	}

	void setStartZoom(int zoom)
	{
		hasStartupZoom = true;
		startupZoom = zoom;
	}

	void setup(File fileMapfile, ConfigBundle configBundle,
			boolean drawTileBorders, boolean drawTileNumbers)
			throws IOException, ClassNotFoundException,
			ParserConfigurationException, SAXException
	{
		// start position
		Mapfile mapfile = MapfileAccess.open(fileMapfile);
		mapfile.close();
		double startLon = startupLon;
		double startLat = startupLat;
		if (!hasStartupPosition) {
			Point start = mapfile.getMetadata().getStart();
			startLon = start.getX();
			startLat = start.getY();
		}
		int startZoom = startupZoom;
		if (!hasStartupZoom) {
			startZoom = 17;
		}

		System.out.println(
				String.format("start position: %f, %f", startLon, startLat));

		setLayout(new GridBagLayout());

		viewer = new MapocadoViewer(fileMapfile, null, configBundle,
				startupZoom, startupLon, startupLat, tileSize);
		viewer.setDrawBorder(drawTileBorders);
		viewer.setDrawTileNumbers(drawTileNumbers);
		viewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		// DEBUG: view tree leafs as overlay
		boolean showTreeLeafs = false;
		if (showTreeLeafs) {
			Mapfile mapfileOverlay = MapfileAccess.open(fileMapfile);
			TreeLeafTileConfig leafConfig = new TreeLeafTileConfig(13, "leafs",
					mapfileOverlay, tileSize);
			viewer.setOverlayTileConfig(leafConfig);
		}

		gpsSimulator = new GpsSimulatorOverlay(viewer);
		gpsSimulator.setEnabled(false);
		viewer.addPaintListener(gpsSimulator);

		Toolbar toolbar = new Toolbar(true, viewer);
		JToggleButton buttonGps = new JToggleButton("fake gps", true);
		buttonGps.setSelected(gpsSimulator.isEnabled());
		toolbar.add(buttonGps);
		buttonGps.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				toggleGps();
			}
		});

		// frame
		JFrame frame = new JFrame("Simple Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.setVisible(true);
		frame.setSize(600, 500);

		// layout
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(viewer, c);

		c.gridy = 0;
		c.weighty = 0.0;
		add(toolbar, c);

		c.gridy = 2;
		c.weighty = 0.0;
		add(new Statusbar(viewer), c);

		viewer.getMapWindow().gotoLonLat(startLon, startLat);
		viewer.getMapWindow().zoom(startZoom);
	}

	protected void toggleGps()
	{
		gpsSimulator.setEnabled(!gpsSimulator.isEnabled());
		viewer.repaint();
	}

	public MapocadoViewer getMapocadoViewer()
	{
		return viewer;
	}
}
