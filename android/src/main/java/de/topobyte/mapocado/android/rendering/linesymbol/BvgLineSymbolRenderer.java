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

package de.topobyte.mapocado.android.rendering.linesymbol;

import android.graphics.Matrix;
import de.topobyte.bvg.BvgAndroidPainter;
import de.topobyte.bvg.BvgImage;

public class BvgLineSymbolRenderer extends CanvasLineSymbolRenderer<BvgImage>
{

	public BvgLineSymbolRenderer(BvgImage symbolImage)
	{
		super(symbolImage);
	}

	@Override
	protected void renderLineSymbol(float x, float y, double sin, double cos)
	{
		canvas.save();
		float scale = (float) (height / symbol.getHeight());
		canvas.translate(x, y);
		Matrix rotation = new Matrix();
		rotation.setSinCos((float) sin, (float) cos);
		canvas.concat(rotation);
		canvas.translate(0, -height / 2f * combinedScaleFactor);
		canvas.scale(combinedScaleFactor, combinedScaleFactor);

		BvgAndroidPainter.draw(canvas, symbol, 0, 0, scale, scale, scale);
		canvas.restore();
	}

}
