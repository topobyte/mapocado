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
