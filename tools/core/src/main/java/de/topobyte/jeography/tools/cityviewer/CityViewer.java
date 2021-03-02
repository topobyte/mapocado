package de.topobyte.jeography.tools.cityviewer;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.locationtech.jts.geom.Point;
import org.xml.sax.SAXException;

import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.tools.cityviewer.action.QuitAction;
import de.topobyte.jeography.tools.cityviewer.action.SearchAction;
import de.topobyte.jeography.tools.cityviewer.theme.Style;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.jeography.viewer.util.ActionUtil;
import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.ConfigBundleReader;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;
import de.topobyte.mapocado.swing.rendering.TreeLeafTileConfig;
import de.topobyte.mapocado.swing.viewer.MapocadoViewer;
import de.topobyte.mapocado.swing.viewer.Statusbar;

public class CityViewer extends JFrame
{
	private static final long serialVersionUID = 8238819723414246980L;

	public static String RES = "res/cityviewer";

	public static String getResource(String path)
	{
		return RES + "/" + path;
	}

	public static String RES_MAPFILE = getResource("city.xmap");
	public static String RES_DATABASE = getResource("city.sqlite");
	public static String RES_STYLES = getResource("styles.xml");

	private MapocadoViewer viewer;

	private Connection connection = null;

	public CityViewer(File fileMapfile, ConfigBundle configBundle,
			File fileDatabase, boolean drawTileBorders, boolean drawTileNumbers,
			boolean hasStartupPosition, double startupLon, double startupLat,
			boolean hasStartupZoom, int startupZoom, List<Style> styles)
			throws IOException, ClassNotFoundException,
			ParserConfigurationException, SAXException
	{
		super("City Viewer");

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

		// database
		try {
			Class.forName("org.sqlite.JDBC").newInstance();
			String url = "jdbc:sqlite:" + fileDatabase;
			if (fileDatabase != null) {
				connection = DriverManager.getConnection(url, "username",
						"password");
			}
		} catch (InstantiationException e) {
			System.out
					.println("InstantiationException while openinng database");
		} catch (IllegalAccessException e) {
			System.out
					.println("IllegalAccessException while openinng database");
		} catch (SQLException e) {
			System.out.println("SQLException while openinng database");
		}

		setLayout(new GridBagLayout());

		int tileSize = Tile.SIZE;

		viewer = new MapocadoViewer(fileMapfile, connection, configBundle,
				startupZoom, startupLon, startupLat, tileSize);
		viewer.setDrawBorder(drawTileBorders);
		viewer.setDrawTileNumbers(drawTileNumbers);
		viewer.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		// DEBUG: view tree leafs as overlay
		boolean showTreeLeafs = false;
		if (showTreeLeafs) {
			Mapfile mapfileOverlay = MapFileAccess.open(fileMapfile);
			TreeLeafTileConfig leafConfig = new TreeLeafTileConfig(13, "leafs",
					mapfileOverlay, tileSize);
			viewer.setOverlayTileConfig(leafConfig);
		}

		Toolbar toolbar = new Toolbar(this, styles);
		toolbar.setFloatable(false);
		JPanel content = new JPanel(new BorderLayout());

		// actions
		QuitAction quit = new QuitAction();
		SearchAction search = new SearchAction(this);

		// input
		InputMap inputMap = viewer
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = viewer.getActionMap();

		ActionUtil.setupMovementActions(viewer, inputMap, actionMap);
		ActionUtil.setupZoomActions(viewer, inputMap, actionMap);

		inputMap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK),
				"ctrl q");
		inputMap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK),
				"ctrl f");
		actionMap.put("ctrl q", quit);
		actionMap.put("ctrl f", search);

		// frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(content);
		setVisible(true);
		setSize(600, 500);

		content.setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		add(viewer, BorderLayout.CENTER);
		add(new Statusbar(viewer), BorderLayout.SOUTH);

		viewer.getMapWindow().gotoLonLat(startLon, startLat);
		viewer.getMapWindow().zoom(startZoom);
	}

	public Viewer getViewer()
	{
		return viewer;
	}

	public Connection getConnection()
	{
		return connection;
	}

	public static ConfigBundle getConfigBundleForStyle(Style style)
			throws IOException, InvalidBundleException
	{
		String filePath = style.getFile();
		String fullPath = getResource(filePath);
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(fullPath);
		InputStream input = url.openStream();
		ConfigBundle configBundle = ConfigBundleReader.readConfigBundle(input);
		return configBundle;
	}

	public void setStyle(Style style) throws IOException,
			ClassNotFoundException, ParserConfigurationException, SAXException
	{
		ConfigBundle configBundle = null;
		try {
			configBundle = getConfigBundleForStyle(style);
		} catch (IOException e) {
			System.out.println("unable to open style");
			e.printStackTrace();
			return;
		} catch (InvalidBundleException e) {
			System.out.println("unable to open style");
			e.printStackTrace();
			return;
		}
		viewer.setStyleFromConfigBundle(configBundle);
	}
}
