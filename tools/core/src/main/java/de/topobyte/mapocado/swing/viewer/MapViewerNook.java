package de.topobyte.mapocado.swing.viewer;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.swing.rendering.TreeLeafTileConfig;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapViewerNook extends JPanel
{

	private static final long serialVersionUID = -7168502470969119340L;

	static final Logger logger = LoggerFactory.getLogger(MapViewerNook.class);

	private boolean hasStartupPosition = false;
	private double startupLon = 0.0;
	private double startupLat = 0.0;
	private boolean hasStartupZoom = false;
	private int startupZoom = 0;

	private MapocadoViewer viewer;
	private GpsSimulatorOverlay gpsSimulator;

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

		setLayout(new GridBagLayout());

		int tileSize = 256;

		viewer = new MapocadoViewer(fileMapfile, null, configBundle,
				startupZoom, startupLon, startupLat, tileSize);
		viewer.setDrawBorder(drawTileBorders);
		viewer.setDrawTileNumbers(drawTileNumbers);
		viewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		viewer.setPreferredSize(new Dimension(600, 1024));

		// DEBUG: view tree leafs as overlay
		boolean showTreeLeafs = false;
		if (showTreeLeafs) {
			Mapfile mapfileOverlay = MapFileAccess.open(fileMapfile);
			TreeLeafTileConfig leafConfig = new TreeLeafTileConfig(13, "leafs",
					mapfileOverlay, tileSize);
			viewer.setOverlayTileConfig(leafConfig);
		}

		gpsSimulator = new GpsSimulatorOverlay(viewer);
		gpsSimulator.setEnabled(false);
		viewer.addPaintListener(gpsSimulator);

		NookToolbar toolbar = new NookToolbar(true, viewer);
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

		// frame
		JFrame frame = new JFrame("Simple Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(this);
		frame.pack();
		frame.setVisible(true);

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

	private class NookToolbar extends Toolbar
	{

		private static final long serialVersionUID = 3105963555931521989L;

		private JButton buttonTakeScreenshot;

		public NookToolbar(boolean showThemeSelector, Viewer... viewers)
		{
			super(showThemeSelector, viewers);

			buttonNames.setVisible(false);
			buttonGrid.setVisible(false);

			buttonTakeScreenshot = new JButton("take screenshot");
			add(buttonTakeScreenshot);
			buttonTakeScreenshot.addActionListener(NookToolbar.this);
		}

		@Override
		public void actionPerformed(ActionEvent event)
		{
			if (event.getSource() == buttonTakeScreenshot) {
				System.out.println("take screenshot");
				try {
					takeScreenshot();
				} catch (AWTException e) {
					System.out.println("error while creating screenshot");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("error while creating screenshot");
					e.printStackTrace();
				}
			} else {
				super.actionPerformed(event);
			}
		}
	}

	public void takeScreenshot() throws AWTException, IOException
	{
		BufferedImage image = new BufferedImage(viewer.getWidth(),
				viewer.getHeight(), BufferedImage.TYPE_INT_ARGB);
		viewer.paint(image.getGraphics());
		File file = File.createTempFile("screen", ".png");
		ImageIO.write(image, "png", file);
	}
}
