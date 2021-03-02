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
