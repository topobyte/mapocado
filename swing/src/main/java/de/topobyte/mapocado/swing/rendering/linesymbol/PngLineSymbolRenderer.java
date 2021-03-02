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
