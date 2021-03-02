package de.topobyte.mapocado.swing.viewer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.viewer.config.TileConfig;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.mapocado.mapformat.MapFileAccess;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.styles.bundled.ConfigBundle;
import de.topobyte.mapocado.styles.bundled.ConfigBundleReader;
import de.topobyte.mapocado.styles.bundled.InvalidBundleException;
import de.topobyte.mapocado.styles.classes.ClassFileHandler;
import de.topobyte.mapocado.styles.classes.ClassFileReader;
import de.topobyte.mapocado.styles.labels.LabelFileHandler;
import de.topobyte.mapocado.styles.labels.LabelFileReader;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.Rule;
import de.topobyte.mapocado.swing.rendering.Conversion;
import de.topobyte.mapocado.swing.rendering.MapImageManager;
import de.topobyte.mapocado.swing.rendering.MapImageSource;
import de.topobyte.mapocado.swing.rendering.MapRenderConfig;
import de.topobyte.mapocado.swing.rendering.MapTileConfig;
import de.topobyte.mapocado.swing.rendering.labels.DbNodePainter;
import de.topobyte.mapocado.swing.theme.Theme;

public class MapocadoViewer extends Viewer
{
	final static Logger logger = LoggerFactory.getLogger(MapocadoViewer.class);

	private static final long serialVersionUID = -183549579718904192L;

	private Connection connection;
	private int tileSize;
	private float tileScaleFactor;
	private DbNodePainter nodePainter = null;
	private MapScalePainter mapScalePainter;
	private CopyrightPainter copyrightPainter;
	private MapRenderConfig renderConfig;

	public MapocadoViewer(File file, Connection connection,
			ConfigBundle configBundle, int zoom, double lon, double lat,
			int tileSize) throws IOException, ClassNotFoundException,
			ParserConfigurationException, SAXException
	{
		super(createTileConfig(file, configBundle, tileSize), null, zoom, lon,
				lat);

		this.connection = connection;
		this.tileSize = tileSize;

		tileScaleFactor = tileSize / (float) Tile.SIZE;

		getMapWindow().setTileSize(tileSize);

		getMapWindow().setMaxZoom(23);

		Mapfile mapfile = MapFileAccess.open(file);
		renderConfig = createMapRenderConfig(configBundle, mapfile);

		if (connection != null) {
			nodePainter = new DbNodePainter(connection, mapfile, renderConfig);
			addPaintListener(nodePainter);
			nodePainter.setTileScaleFactor(tileScaleFactor);
		}

		mapScalePainter = new MapScalePainter();
		addPaintListener(mapScalePainter);
		mapScalePainter.setVerticalOffset(24);

		copyrightPainter = new CopyrightPainter();
		addPaintListener(copyrightPainter);
		copyrightPainter.setVerticalOffset(2);

		updateVariousSettings(renderConfig);

		setDrawOverlay(true);

		setDrawCrosshair(false);
		setMouseActive(true);

		addMouseListener(new ViewerMouseListener(this, false));

		if (nodePainter != null) {
			OverlayActivatationManager overlayActivatationManager = new OverlayActivatationManager(
					this, nodePainter, 500);
			getMapWindow().addChangeListener(overlayActivatationManager);
		}
	}

	public MapRenderConfig getRenderConfig()
	{
		return renderConfig;
	}

	public void setScaleFactor(float scaleFactor)
	{
		if (nodePainter != null) {
			nodePainter.setUserScaleFactor(scaleFactor);
		}
		MapImageManager mim = (MapImageManager) getImageManagerBase();
		MapImageSource imageSource = (MapImageSource) mim.getImageSource();
		imageSource.setUserScaleFactor(scaleFactor);
	}

	public float getScaleFactor()
	{
		MapImageManager mim = (MapImageManager) getImageManagerBase();
		MapImageSource imageSource = (MapImageSource) mim.getImageSource();
		return imageSource.getUserScaleFactor();
	}

	public void setStyleFromTheme(Theme theme) throws IOException,
			ClassNotFoundException, ParserConfigurationException, SAXException
	{
		String configFile = theme.getPath();

		logger.info("reading style package: " + configFile);
		ConfigBundle configBundle = null;
		try {
			configBundle = ConfigBundleReader
					.readConfigBundle(new File(configFile));
		} catch (IOException e) {
			logger.error("unable to read style package (IOException): "
					+ e.getMessage(), e);
			System.exit(1);
		} catch (InvalidBundleException e) {
			logger.error("unable to read style package (Invalid bundle): "
					+ e.getMessage(), e);
			System.exit(1);
		}

		setStyleFromConfigBundle(configBundle);
	}

	public void setStyleFromConfigBundle(ConfigBundle configBundle)
			throws IOException, ClassNotFoundException,
			ParserConfigurationException, SAXException
	{
		TileConfig config = this.getTileConfig();
		if (!(config instanceof MapTileConfig)) {
			return;
		}

		MapTileConfig mtc = (MapTileConfig) config;
		Mapfile mapfile = mtc.getMapfile();

		renderConfig = createMapRenderConfig(configBundle, mapfile);

		updateVariousSettings(renderConfig);

		MapTileConfig tileConfig = new MapTileConfig(133, "foo", mapfile,
				renderConfig, tileSize);
		setTileConfig(tileConfig);

		if (nodePainter != null) {
			nodePainter.setup(connection, renderConfig);
		}

		repaint();
	}

	private static TileConfig createTileConfig(File file,
			ConfigBundle configBundle, int tileSize) throws IOException,
			ClassNotFoundException, ParserConfigurationException, SAXException
	{
		Mapfile mapfile = MapFileAccess.open(file);
		MapRenderConfig renderConfig = createMapRenderConfig(configBundle,
				mapfile);
		MapTileConfig mapTileConfig = new MapTileConfig(101, "MapTiles",
				mapfile, renderConfig, tileSize);
		return mapTileConfig;
	}

	private static MapRenderConfig createMapRenderConfig(
			ConfigBundle configBundle, Mapfile mapfile)
			throws ParserConfigurationException, SAXException, IOException
	{
		logger.info("reading style classes from style package");
		ClassFileHandler classes = null;
		classes = ClassFileReader.read(configBundle.getClassesAsInputStream());

		LabelFileHandler labelHandler = LabelFileReader
				.read(configBundle.getLabelsAsInputStream());
		List<Rule> rules = labelHandler.getRules();
		Map<Rule, LabelContainer> styles = labelHandler.getRuleToLabel();

		MapRenderConfig renderConfig = new MapRenderConfig(
				classes.getBackground(), classes.getOverlayInner(),
				classes.getOverlayOuter(), classes.getOverlayGpsInner(),
				classes.getOverlayGpsOuter(), classes.getObjectClasses(), rules,
				styles, configBundle);

		renderConfig
				.createSlimClasses(mapfile.getMetadata().getPoolForKeepKeys());

		return renderConfig;
	}

	private void updateVariousSettings(MapRenderConfig renderConfig)
	{
		Color bg = Conversion.getColor(renderConfig.getBackgroundColor());
		setColorBackground(bg);

		Color inner = Conversion.getColor(renderConfig.getOverlayInner());
		Color outer = Conversion.getColor(renderConfig.getOverlayOuter());
		mapScalePainter.setColorBarInline(inner);
		mapScalePainter.setColorBarOutline(outer);
		mapScalePainter.setColorTextInline(inner);
		mapScalePainter.setColorTextOutline(outer);
		copyrightPainter.setColorTextInline(inner);
		copyrightPainter.setColorTextOutline(outer);
	}

}
