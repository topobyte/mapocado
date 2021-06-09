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

package de.topobyte.mapocado.swing.rendering.pathtext;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.rendering.Clipping;
import de.topobyte.mapocado.rendering.pathtext.LabelType;
import de.topobyte.mapocado.rendering.pathtext.PathLabeller;
import de.topobyte.mapocado.rendering.text.TextIntersectionChecker;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;
import de.topobyte.mapocado.swing.rendering.Conversion;
import de.topobyte.mapocado.swing.rendering.GeometryTransformation;
import de.topobyte.mapocado.swing.rendering.geom.GeneralRectangleAwt;

public class AwtPathLabeller extends PathLabeller
{

	private Graphics2D g;
	private Font font;
	private Color color;

	public AwtPathLabeller(Graphics2D g, TextIntersectionChecker checker,
			Clipping hitTest, int zoom, LengthTransformer mercator,
			float combinedScaleFactor)
	{
		super(checker, hitTest, zoom, mercator, combinedScaleFactor);
		this.g = g;
	}

	@Override
	public void setStyle(PathTextSlim pathText)
	{
		super.setStyle(pathText);
		String fontFamily = Conversion.getFontFamily(pathText.getFontFamily());
		int style = Conversion.getFontStyle(pathText.getFontStyle());

		font = new Font(fontFamily, style, (int) Math.round(fontSize));

		color = Conversion.getColor(pathText.getFill());
	}

	@Override
	protected double getTextLength(String labelText)
	{
		return AwtTextUtil.getTextWidth(font, labelText);
	}

	@Override
	protected void render(Linestring string, String labelText,
			float pathLengthStorage, float offset, float paddedTextLength,
			boolean reverse, int[][] boxes, boolean chunked, LabelType type)
	{
		Path2D path = GeometryTransformation.getPath(string, mercator);
		TextPath line = AwtTextUtil.createLine(path, paddedTextLength, offset);
		Path2D p = line.getPath();

		if (debugLabelPlacement) {
			drawDebugLabelPlacement(p, line, boxes, chunked, type);
		}

		TextStroke stroke = new TextStroke(labelText, font);
		g.setColor(color);
		g.setStroke(stroke);

		if (reverse) {
			p = AwtTextUtil.reverse(p);
		}

		g.draw(p);
	}

	protected void drawDebugLabelPlacement(Path2D path, TextPath line,
			int[][] boxes, boolean chunked, LabelType type)
	{
		Color c;
		if (!chunked) {
			c = type == LabelType.SIMPLE1 ? Color.RED
					: type == LabelType.SIMPLE2 ? Color.ORANGE
							: type == LabelType.OPTIMIZED ? Color.YELLOW
									: Color.GREEN;
		} else {
			c = type == LabelType.SIMPLE1 ? Color.BLUE
					: type == LabelType.SIMPLE2 ? Color.MAGENTA
							: type == LabelType.OPTIMIZED ? Color.CYAN
									: Color.PINK;
		}

		/* DEBUG: render path */
		g.setColor(c);
		g.setStroke(new BasicStroke(4));
		g.draw(path);

		/* DEBUG: render text boxes... */
		g.setColor(Color.LIGHT_GRAY);
		for (int[] box : boxes) {
			Path2D rect = GeneralRectangleAwt.createPath(box, mercator);
			g.fill(rect);
		}

		/* DEBUG: render text path */
		g.setColor(Color.GREEN);
		g.setStroke(new BasicStroke(2.0f));
		g.draw(line.getPath());
	}

	@Override
	protected void drawStringForDebugging(Linestring string)
	{
		int lw = 4;
		int ps = 6;
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(lw));
		g.draw(GeometryTransformation.getPath(string, mercator));
		float x = mercator.getX(string.x[0]);
		float y = mercator.getY(string.y[0]);
		g.setColor(Color.BLUE);
		g.fillRect(Math.round(x - ps / 2), Math.round(y - ps / 2), ps, ps);
		x = mercator.getX(string.x[string.x.length - 1]);
		y = mercator.getY(string.y[string.x.length - 1]);
		g.setColor(Color.MAGENTA);
		g.fillRect(Math.round(x - ps / 2), Math.round(y - ps / 2), ps, ps);
		g.setColor(Color.GREEN);
		for (int i = 1; i < string.x.length - 1; i++) {
			x = mercator.getX(string.x[i]);
			y = mercator.getY(string.y[i]);
			g.fillRect(Math.round(x - ps / 2), Math.round(y - ps / 2), ps, ps);
		}
	}
}
