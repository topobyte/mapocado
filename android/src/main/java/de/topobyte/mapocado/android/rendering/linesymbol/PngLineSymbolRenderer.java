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
