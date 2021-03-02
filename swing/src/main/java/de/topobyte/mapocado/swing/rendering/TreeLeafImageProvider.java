package de.topobyte.mapocado.swing.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import de.topobyte.adt.geo.BBox;
import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.core.TileUtil;
import de.topobyte.jeography.tiles.source.ImageProvider;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.mapformat.rtree.disk.Entry;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class TreeLeafImageProvider extends ImageProvider<Tile, BufferedImage>
{

	private static final Color colorFillTile = new Color(0x33ff0000, true);
	private static final Color colorLineTile = new Color(0xff000000, true);

	private static final Color colorFill = new Color(0x09ff0000, true);
	private static final Color colorLines = new Color(0x99ff0000, true);

	private final Mapfile mapfile;

	boolean fillTile = false;
	boolean drawBorder = false;

	private int tileSize;

	public TreeLeafImageProvider(Mapfile mapfile, int nThreads, int tileSize)
	{
		super(nThreads);
		this.mapfile = mapfile;
		this.tileSize = tileSize;
	}

	@Override
	public BufferedImage load(Tile tile)
	{
		BufferedImage image = new BufferedImage(tileSize, tileSize,
				BufferedImage.TYPE_4BYTE_ABGR);

		Graphics2D g = image.createGraphics();

		if (fillTile) {
			g.setColor(colorFillTile);
			g.fillRect(0, 0, tileSize, tileSize);
		}

		int off = 10;
		int border = 1;
		if (drawBorder) {
			Graphics2D graphics = image.createGraphics();
			graphics.setColor(colorLineTile);
			graphics.drawRect(off + border, off + border,
					tileSize - off * 2 - border * 2,
					tileSize - off * 2 - border * 2);
		}

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		create(tile, g);

		return image;
	}

	private void create(Tile tile, Graphics2D g)
	{
		System.out.println(String.format("tile: zoom: %d, x: %d, y: %d",
				tile.getZoom(), tile.getTx(), tile.getTy()));
		BBox bbox = TileUtil.getBoundingBox(tile);
		BBox bboxRequest = TileUtil.getBoundingBox(tile.getTx() - 0.5,
				tile.getTx() + 1.5, tile.getTy() - 0.5, tile.getTy() + 1.5,
				tile.getZoom());
		System.out.println(bbox);

		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();

		// MapWindow mapWindow = new MapWindow(bbox, tile.getZoom());

		BoundingBox rectRequest = new BoundingBox(bboxRequest.getLon1(),
				bboxRequest.getLon2(), bboxRequest.getLat1(),
				bboxRequest.getLat2(), true);

		List<DiskTree<Way>> trees = treeWays.getObjects(tile.getZoom());
		for (DiskTree<Way> tree : trees) {
			List<Entry> leafEntries;
			try {
				leafEntries = tree.intersectionQueryForLeafs(rectRequest);
				render(g, tile, leafEntries);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void render(Graphics2D g, Tile tile, List<Entry> leafEntries)
	{
		Mercator mercator = new Mercator(tile, tileSize);

		for (Entry leaf : leafEntries) {
			Linestring string = createLine(leaf);
			Path2D path = GeometryTransformation.getPath(string, mercator);
			g.setColor(colorFill);
			g.fill(path);
			g.setColor(colorLines);
			g.draw(path);
		}
	}

	private Linestring createLine(Entry leaf)
	{
		int[] xs = new int[5];
		int[] ys = new int[5];

		xs[0] = leaf.getX1();
		xs[1] = leaf.getX2();
		xs[2] = leaf.getX2();
		xs[3] = leaf.getX1();
		xs[4] = leaf.getX1();

		ys[0] = leaf.getY1();
		ys[1] = leaf.getY1();
		ys[2] = leaf.getY2();
		ys[3] = leaf.getY2();
		ys[4] = leaf.getY1();

		return new Linestring(xs, ys);
	}

}
