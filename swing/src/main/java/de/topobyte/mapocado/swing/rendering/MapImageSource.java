package de.topobyte.mapocado.swing.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.core.TileUtil;
import de.topobyte.jeography.tiles.source.ImageSource;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.rendering.Clipping;
import de.topobyte.mapocado.rendering.pathtext.LinestringUtil;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapImageSource extends MapImageRenderer
		implements ImageSource<Tile, BufferedImage>
{
	final static Logger logger = LoggerFactory.getLogger(MapImageSource.class);

	private int tileSize;
	private float userScaleFactor = 1;
	private float tileScaleFactor = 1;

	public MapImageSource(Mapfile mapfile, MapRenderConfig renderConfig,
			int tileSize)
	{
		super(mapfile, renderConfig);
		this.tileSize = tileSize;
	}

	@Override
	public BufferedImage load(Tile tile)
	{
		logger.debug(String.format("producing tile: %d,%d,%d", tile.getZoom(),
				tile.getTx(), tile.getTy()));
		BufferedImage image = new BufferedImage(tileSize, tileSize,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int off = 10;
		int border = 1;
		boolean drawBorder = true;
		if (drawBorder) {
			Graphics2D graphics = image.createGraphics();
			graphics.setColor(new Color(0xff000000, true));
			graphics.drawRect(off + border, off + border,
					tileSize - off * 2 - border * 2,
					tileSize - off * 2 - border * 2);
		}

		create(tile, g, tileSize);

		return image;
	}

	private double QUERY_RATIO_WAY = 1.1;
	private double QUERY_RATIO_NODE = 1.5;
	private double ADD_WAY = QUERY_RATIO_WAY;
	private double SUB_WAY = QUERY_RATIO_WAY - 1;
	private double ADD_NODE = QUERY_RATIO_NODE;
	private double SUB_NODE = QUERY_RATIO_NODE - 1;

	public void setTileSize(int tileSize)
	{
		this.tileSize = tileSize;
		calculateCombinedScaleFactor();
	}

	public void setUserScaleFactor(float userScaleFactor)
	{
		this.userScaleFactor = userScaleFactor;
		calculateCombinedScaleFactor();
	}

	public float getUserScaleFactor()
	{
		return userScaleFactor;
	}

	private void calculateCombinedScaleFactor()
	{
		combinedScaleFactor = userScaleFactor * tileScaleFactor;
	}

	private void create(Tile tile, Graphics2D g, int tileSize)
	{
		logger.debug(String.format("tile: zoom: %d, x: %d, y: %d",
				tile.getZoom(), tile.getTx(), tile.getTy()));

		tileScaleFactor = tileSize / (float) Tile.SIZE;
		calculateCombinedScaleFactor();

		BBox bbox = TileUtil.getBoundingBox(tile);
		int zoom = tile.getZoom();
		double extra = zoom < 20 ? 0 : zoom / (double) 20 * 0.2;
		BBox bboxRequestWay = TileUtil.getBoundingBox(
				tile.getTx() - SUB_WAY - extra, tile.getTx() + ADD_WAY + extra,
				tile.getTy() - SUB_WAY - extra, tile.getTy() + ADD_WAY + extra,
				tile.getZoom());
		BBox bboxRequestNode = TileUtil.getBoundingBox(tile.getTx() - SUB_NODE,
				tile.getTx() + ADD_NODE, tile.getTy() - SUB_NODE,
				tile.getTy() + ADD_NODE, tile.getZoom());
		logger.debug("bbox: " + bbox);

		Clipping clipping = new Clipping(tileSize, tileSize,
				clippingFactor(zoom));
		Clipping hitTest = LinestringUtil.createClipping(tile, 1.05f);

		Mercator mercator = new Mercator(tile, tileSize);

		create(g, mercator, bboxRequestNode, bboxRequestWay, tile.getZoom(),
				tileSize, tileSize, clipping, hitTest);
	}

	private static int clippingFactor(int zoom)
	{
		if (zoom <= 12) {
			return zoom;
		} else if (zoom <= 16) {
			return zoom * 2;
		} else if (zoom <= 20) {
			return zoom * 3;
		} else {
			return zoom * 4;
		}
		// int clip = zoom;
		// if (zoom >= 20) {
		// clip += 1 << (zoom - 17);
		// }
	}

}
