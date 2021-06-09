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

package de.topobyte.mapocado.swing.rendering;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.adt.geo.BBox;
import de.topobyte.mapocado.mapformat.Geo;
import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.rendering.Clipping;
import de.topobyte.mercator.image.MercatorImage;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapImageCreator extends MapImageRenderer
{
	final static Logger logger = LoggerFactory.getLogger(MapImageCreator.class);

	private double realZoom = 10;
	private int zoom = 10;

	public MapImageCreator(Mapfile mapfile, MapRenderConfig renderConfig)
	{
		super(mapfile, renderConfig);
	}

	public int getZoom()
	{
		return zoom;
	}

	public double getRealZoom()
	{
		return realZoom;
	}

	public void setZoom(int zoom, double realZoom)
	{
		this.zoom = zoom;
		this.realZoom = realZoom;
		combinedScaleFactor = (float) (Math.pow(2, realZoom)
				/ Math.pow(2, zoom));
	}

	public BufferedImage load(BBox boundingBox, int width, int height)
	{
		BufferedImage image = new BufferedImage(width, height,
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
					256 - off * 2 - border * 2, 256 - off * 2 - border * 2);
		}

		create(boundingBox, g, width, height);

		return image;
	}

	private float scaleFactor = 1;

	public void setScaleFactor(float scaleFactor)
	{
		this.scaleFactor = scaleFactor;
	}

	public float getScaleFactor()
	{
		return scaleFactor;
	}

	private void create(BBox bbox, Graphics2D g, int width, int height)
	{
		MercatorImage mapImage = new MercatorImage(bbox, width, height);
		// double extra = zoom < 20 ? 0 : zoom / (double) 20 * 0.2;
		// BBox bboxRequestWay = TileUtil.getBoundingBox(tile.getTx() - SUB_WAY
		// - extra, tile.getTx() + ADD_WAY + extra, tile.getTy() - SUB_WAY
		// - extra, tile.getTy() + ADD_WAY + extra, tile.getZoom());
		// BBox bboxRequestNode = TileUtil.getBoundingBox(tile.getTx() -
		// SUB_NODE,
		// tile.getTx() + ADD_NODE, tile.getTy() - SUB_NODE, tile.getTy()
		// + ADD_NODE, tile.getZoom());
		BBox bboxRequestWay = bbox;
		BBox bboxRequestNode = bbox;
		logger.debug("bbox: " + bbox);

		Clipping clipping = new Clipping(0, width, 0, height);

		int x1 = Geo.mercatorFromLongitude(bbox.getLon1());
		int x2 = Geo.mercatorFromLongitude(bbox.getLon2());
		int y1 = Geo.mercatorFromLatitude(bbox.getLat1());
		int y2 = Geo.mercatorFromLatitude(bbox.getLat2());
		Clipping hitTest = new Clipping(x1, x2, y1, y2);

		MapImageTransformer transformer = new MapImageTransformer(mapImage);

		create(g, transformer, bboxRequestNode, bboxRequestWay, zoom, width,
				height, clipping, hitTest);
	}

	private class MapImageTransformer implements LengthTransformer
	{

		private final MercatorImage mapImage;

		public MapImageTransformer(MercatorImage mapImage)
		{
			this.mapImage = mapImage;
		}

		@Override
		public float getX(int x)
		{
			return (float) ((x / (double) (Geo.MERCATOR_SIZE)
					* mapImage.getWorldSize() - mapImage.getImageSx()));
		}

		@Override
		public float getY(int y)
		{
			return (float) ((y / (double) (Geo.MERCATOR_SIZE)
					* mapImage.getWorldSize() - mapImage.getImageSy()));
		}

		@Override
		public double getLengthStorageUnits(double length, int zoom)
		{
			return Mercator.getLengthStorageUnitsDefault(length, zoom)
					/ combinedScaleFactor;
		}

		@Override
		public double getLengthTileUnits(double length, int zoom)
		{
			System.out.println(
					"length to tile units: " + length + ", zoom: " + zoom);
			System.out.println("result: "
					+ Mercator.getLengthTileUnitsDefault(length, zoom));
			System.out.println("result: "
					+ Mercator.getLengthTileUnitsDefault(length, zoom)
							* combinedScaleFactor);
			return Mercator.getLengthTileUnitsDefault(length, zoom)
					* combinedScaleFactor;
		}

	}
}
