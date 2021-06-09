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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import de.topobyte.jeography.core.mapwindow.TileMapWindow;
import de.topobyte.jeography.viewer.core.PaintListener;

public class CopyrightPainter implements PaintListener
{
	private final static String notice = "Mapdata by Openstreetmap";

	private Color colorTextOutline = Color.WHITE;
	private Color colorTextInline = Color.BLACK;

	private int fontSize = 12;
	private int offsetX = 10;
	private int offsetY = 5;

	@Override
	public void onPaint(TileMapWindow mapWindow, Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;

		int height = mapWindow.getHeight();
		String text = notice;

		Font font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		GlyphVector glyphVector = font.createGlyphVector(frc, text);
		Shape outline = glyphVector.getOutline();
		FontMetrics metrics = graphics.getFontMetrics(font);

		// remember state
		AffineTransform backup = g.getTransform();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform transform = new AffineTransform(backup);
		transform.translate(offsetX,
				height - offsetY - metrics.getHeight() / 2);
		g.setTransform(transform);

		g.setColor(colorTextOutline);
		g.draw(outline);
		g.setColor(colorTextInline);
		g.fill(outline);

		// restore state
		g.setTransform(backup);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
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
