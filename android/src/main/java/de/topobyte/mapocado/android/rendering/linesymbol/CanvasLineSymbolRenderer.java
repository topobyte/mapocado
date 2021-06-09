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

import android.graphics.Canvas;
import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.rendering.linesymbol.LineSymbolRenderer;

public abstract class CanvasLineSymbolRenderer<T> extends LineSymbolRenderer<T>
{

	protected Canvas canvas;

	public CanvasLineSymbolRenderer(T symbolImage)
	{
		super(symbolImage);
	}

	public void init(Canvas canvas, LengthTransformer transformer, float height,
			double widthStorage, double offsetStorage,
			float combinedScaleFactor)
	{
		super.init(transformer, height, widthStorage, offsetStorage,
				combinedScaleFactor);
		this.canvas = canvas;
	}
}
