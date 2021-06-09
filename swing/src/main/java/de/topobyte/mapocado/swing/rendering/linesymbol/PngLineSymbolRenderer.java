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

package de.topobyte.mapocado.swing.rendering.linesymbol;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class PngLineSymbolRenderer extends AwtLineSymbolRenderer<BufferedImage>
{

	public PngLineSymbolRenderer(BufferedImage symbolImage)
	{
		super(symbolImage);
	}

	@Override
	protected void renderLineSymbol(float x, float y, double sin, double cos)
	{
		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		t.concatenate(new AffineTransform(cos, sin, -sin, cos, 0, 0));
		t.translate(0, -height / 2.0 * combinedScaleFactor);
		t.scale(combinedScaleFactor, combinedScaleFactor);
		g.drawImage(symbol, t, null);
	}
}
