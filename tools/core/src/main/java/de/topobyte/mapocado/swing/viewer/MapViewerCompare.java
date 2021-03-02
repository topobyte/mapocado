package de.topobyte.mapocado.swing.viewer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.viewer.config.Configuration;
import de.topobyte.jeography.viewer.config.TileConfig;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapViewerCompare extends JPanel
{

	private static final long serialVersionUID = -7168502470969119340L;

	static final Logger logger = LoggerFactory
			.getLogger(MapViewerCompare.class);

	private boolean hasStartupPosition = false;
	private double startupLon = 0.0;
	private double startupLat = 0.0;
	private boolean hasStartupZoom = false;
	private int startupZoom = 0;

	private MapocadoViewer viewer;

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
		Mapfile mapfile = MapFileAccess.open(fileMapfile);
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

		JFrame frame = new JFrame("Comparing Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Configuration config = Configuration.createDefaultConfiguration();
		TileConfig tileConfig = config.getTileConfigs().get(0);

		setLayout(new GridBagLayout());

		/*
		 * mapocado viewer
		 */

		viewer = new MapocadoViewer(fileMapfile, null, configBundle,
				startupZoom, startupLon, startupLat, Tile.SIZE);
		viewer.setDrawBorder(drawTileBorders);
		viewer.setDrawTileNumbers(drawTileNumbers);

		/*
		 * second comparison viewer
		 */

		Viewer viewer2 = new Viewer(tileConfig, null);
		viewer2.setDrawCrosshair(false);
		viewer2.setDrawBorder(drawTileBorders);
		viewer2.setDrawTileNumbers(drawTileNumbers);
		viewer2.setMouseActive(true);

		/*
		 * synchronization
		 */

		new SynchronizingMapWindowChangeListener(viewer, viewer2);

		/*
		 * frame
		 */

		viewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		viewer2.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		frame.setContentPane(this);
		frame.setVisible(true);
		frame.setSize(800, 500);

		viewer.getMapWindow().gotoLonLat(startLon, startLat);
		viewer.getMapWindow().zoom(startZoom);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(viewer, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(viewer2, c);

		c.gridwidth = 2;
		c.gridx = 0;

		c.gridy = 0;
		c.weighty = 0.0;
		add(new Toolbar(true, viewer, viewer2), c);

		c.gridy = 2;
		c.weighty = 0.0;
		add(new Statusbar(viewer), c);
	}

	public MapocadoViewer getMapocadoViewer()
	{
		return viewer;
	}
}
