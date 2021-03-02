package de.topobyte.mapocado.swing.rendering.linesymbol;

import java.awt.Graphics2D;

import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.rendering.linesymbol.LineSymbolRenderer;

public abstract class AwtLineSymbolRenderer<T> extends LineSymbolRenderer<T>
{

	protected Graphics2D g;

	public AwtLineSymbolRenderer(T symbolImage)
	{
		super(symbolImage);
	}

	public void init(Graphics2D g, LengthTransformer transformer, float height,
			double widthStorage, double offsetStorage,
			float combinedScaleFactor)
	{
		super.init(transformer, height, widthStorage, offsetStorage,
				combinedScaleFactor);
		this.g = g;
	}

}
