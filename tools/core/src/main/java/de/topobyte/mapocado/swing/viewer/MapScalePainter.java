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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import de.topobyte.jeography.core.mapwindow.MapWindow;
import de.topobyte.jeography.core.mapwindow.TileMapWindow;
import de.topobyte.jeography.viewer.core.PaintListener;
import de.topobyte.util.maps.MercatorUtil;
import de.topobyte.util.maps.scalebar.MapScaleBar;
import de.topobyte.util.maps.scalebar.MapScaleChecker;

public class MapScalePainter implements PaintListener
{
	private MapScaleChecker mapScaleChecker;

	private int maxWidth = 150;
	private int offsetX = 10;
	private int offsetY = 10;

	private int innerLineWidth = 2;
	private int outerLineWidth = 4;
	private int heightBar = 15;
	private int fontSize = 12;

	private Color colorBarOutline = Color.WHITE;
	private Color colorBarInline = Color.BLACK;

	private Color colorTextOutline = Color.WHITE;
	private Color colorTextInline = Color.BLACK;

	public MapScalePainter()
	{
		mapScaleChecker = new MapScaleChecker(maxWidth);
	}

	@Override
	public void onPaint(TileMapWindow mapWindow, Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;

		double lat = mapWindow.getCenterLat();
		int zoom = mapWindow.getZoomLevel();
		double metersPerPixel = MercatorUtil.calculateGroundResolution(lat,
				zoom, mapWindow.getTileWidth());
		MapScaleBar mapScaleBar = mapScaleChecker
				.getAppropriate(metersPerPixel);

		drawBar(mapWindow, g, mapScaleBar);
	}

	private void drawBar(MapWindow mapWindow, Graphics2D g,
			MapScaleBar mapScaleBar)
	{
		int height = mapWindow.getHeight();

		String text = createText(mapScaleBar);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int y0 = height - offsetY - heightBar;
		int ym = height - offsetY - heightBar / 2;
		int y1 = height - offsetY;
		int w = mapScaleBar.getPixels();

		// outline
		g.setColor(colorBarOutline);
		g.setStroke(new BasicStroke(outerLineWidth));
		g.drawLine(offsetX, ym, offsetX + w, ym);
		g.drawLine(offsetX, y0, offsetX, y1);
		g.drawLine(offsetX + w, y0, offsetX + w, y1);

		// inline
		g.setColor(colorBarInline);
		g.setStroke(new BasicStroke(innerLineWidth));
		g.drawLine(offsetX, ym, offsetX + w, ym);
		g.drawLine(offsetX, y0, offsetX, y1);
		g.drawLine(offsetX + w, y0, offsetX + w, y1);

		// text
		Font font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		GlyphVector glyphVector = font.createGlyphVector(frc, text);
		Shape outline = glyphVector.getOutline();

		AffineTransform backup = g.getTransform();
		AffineTransform transform = new AffineTransform(backup);
		transform.translate(offsetX + fontSize, height - offsetY - fontSize);
		g.setTransform(transform);

		g.setColor(colorTextOutline);
		g.draw(outline);
		g.setColor(colorTextInline);
		g.fill(outline);

		g.setTransform(backup);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private String createText(MapScaleBar mapScaleBar)
	{
		int meters = mapScaleBar.getMeters();
		if (meters > 1000) {
			return String.format("%d km", meters / 1000);
		}
		return String.format("%d m", meters);
	}

	public Color getColorBarOutline()
	{
		return colorBarOutline;
	}

	public void setColorBarOutline(Color colorBarOutline)
	{
		this.colorBarOutline = colorBarOutline;
	}

	public Color getColorBarInline()
	{
		return colorBarInline;
	}

	public void setColorBarInline(Color colorBarInline)
	{
		this.colorBarInline = colorBarInline;
	}

	public Color getColorTextOutline()
	{
		return colorTextOutline;
	}

	public void setColorTextOutline(Color colorTextOutline)
	{
		this.colorTextOutline = colorTextOutline;
	}

	public Color getColorTextInline()
	{
		return colorTextInline;
	}

	public void setColorTextInline(Color colorTextInline)
	{
		this.colorTextInline = colorTextInline;
	}

	public void setVerticalOffset(int offset)
	{
		offsetY = offset;
	}

	public void setHorizontalOffset(int offset)
	{
		offsetX = offset;
	}

}
