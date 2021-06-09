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

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;

public class PngLineSymbolRenderer extends CanvasLineSymbolRenderer<Bitmap>
{

	private Paint paint = new Paint();

	public PngLineSymbolRenderer(Bitmap symbolImage)
	{
		super(symbolImage);
	}

	@Override
	protected void renderLineSymbol(float x, float y, double sin, double cos)
	{
		canvas.save();
		canvas.translate(x, y);
		Matrix rotation = new Matrix();
		rotation.setSinCos((float) sin, (float) cos);
		canvas.concat(rotation);
		canvas.translate(0, -height / 2f * combinedScaleFactor);
		canvas.scale(combinedScaleFactor, combinedScaleFactor);

		canvas.drawBitmap(symbol, 0, 0, paint);
		canvas.restore();
	}

}
