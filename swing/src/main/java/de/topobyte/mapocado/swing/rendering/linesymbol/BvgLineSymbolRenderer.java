package de.topobyte.mapocado.swing.rendering.linesymbol;

import java.awt.geom.AffineTransform;

import de.topobyte.bvg.BvgAwtPainter;
import de.topobyte.bvg.BvgImage;

public class BvgLineSymbolRenderer extends AwtLineSymbolRenderer<BvgImage>
{

	public BvgLineSymbolRenderer(BvgImage symbolImage)
	{
		super(symbolImage);
	}

	@Override
	protected void renderLineSymbol(float x, float y, double sin, double cos)
	{
		float scale = (float) (height / symbol.getHeight());
		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		t.concatenate(new AffineTransform(cos, sin, -sin, cos, 0, 0));
		t.translate(0, -height / 2.0 * combinedScaleFactor);
		t.scale(combinedScaleFactor, combinedScaleFactor);

		AffineTransform backup = g.getTransform();
		g.transform(t);
		BvgAwtPainter.draw(g, symbol, 0, 0, scale, scale);
		g.setTransform(backup);
	}
}
