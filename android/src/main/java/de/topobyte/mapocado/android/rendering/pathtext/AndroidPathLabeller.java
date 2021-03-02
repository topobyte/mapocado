package de.topobyte.mapocado.android.rendering.pathtext;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import de.topobyte.mapocado.android.rendering.GeometryConversion;
import de.topobyte.mapocado.android.rendering.StyleConversion;
import de.topobyte.mapocado.android.rendering.geom.GeneralRectangleAndroid;
import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.rendering.Clipping;
import de.topobyte.mapocado.rendering.pathtext.LabelType;
import de.topobyte.mapocado.rendering.pathtext.PathLabeller;
import de.topobyte.mapocado.rendering.text.TextIntersectionChecker;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;

public class AndroidPathLabeller extends PathLabeller
{

	private Canvas canvas;

	private Paint fill;
	private Paint stroke;

	private float vOffset;

	public AndroidPathLabeller(Canvas canvas, TextIntersectionChecker checker,
			Clipping hitTest, int zoom, LengthTransformer mercator,
			float combinedScaleFactor)
	{
		super(checker, hitTest, zoom, mercator, combinedScaleFactor);
		this.canvas = canvas;
	}

	@Override
	public void setStyle(PathTextSlim pathText)
	{
		super.setStyle(pathText);

		Typeface typeface = StyleConversion
				.getFontFamily(pathText.getFontFamily());
		int fontStyle = StyleConversion.getFontStyle(pathText.getFontStyle());
		int fillColor = StyleConversion.getColor(pathText.getFill());
		int strokeColor = StyleConversion.getColor(pathText.getStroke());
		typeface = Typeface.create(typeface, fontStyle);

		vOffset = fontSize / 4;

		fill = new Paint(Paint.ANTI_ALIAS_FLAG);
		fill.setStyle(Paint.Style.FILL);
		fill.setTypeface(typeface);
		fill.setTextSize(fontSize);
		fill.setColor(fillColor);

		stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		stroke.setStyle(Paint.Style.STROKE);
		stroke.setTypeface(typeface);
		stroke.setTextSize(fontSize);
		stroke.setColor(strokeColor);
		stroke.setStrokeWidth(pathText.getStrokeWidth());

	}

	@Override
	protected double getTextLength(String labelText)
	{
		float textLen = fill.measureText(labelText);
		return textLen;
	}

	@Override
	protected void render(Linestring string, String labelText,
			float pathLengthStorage, float offset, float paddedTextLength,
			boolean reverse, int[][] boxes, boolean chunked, LabelType type)
	{
		// create text path and calculate values
		Path path = new Path();
		GeometryConversion.createPath(path, string, reverse, mercator);

		double pathLength = mercator.getLengthTileUnits(pathLengthStorage,
				zoom);
		if (reverse) {
			offset = (float) (pathLength - offset - paddedTextLength);
		}

		if (debugLabelPlacement) {
			drawDebugLabelPlacement(path, boxes, chunked, type);
		}

		float hOffset = offset + scaledPadding;

		canvas.drawTextOnPath(labelText, path, hOffset, vOffset, stroke);
		canvas.drawTextOnPath(labelText, path, hOffset, vOffset, fill);
	}

	protected void drawDebugLabelPlacement(Path path, int[][] boxes,
			boolean chunked, LabelType type)
	{
		int c;
		if (!chunked) {
			c = type == LabelType.SIMPLE1 ? Color.RED
					: type == LabelType.SIMPLE2 ? 0xffa500 // orange
							: type == LabelType.OPTIMIZED ? Color.YELLOW
									: Color.GREEN;
		} else {
			c = type == LabelType.SIMPLE1 ? Color.BLUE
					: type == LabelType.SIMPLE2 ? Color.MAGENTA
							: type == LabelType.OPTIMIZED ? Color.CYAN
									: 0xffc0cb; // pink
		}

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		/* DEBUG: render path */
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		paint.setColor(c);
		canvas.drawPath(path, paint);

		/* DEBUG: render text boxes... */
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		Path p = new Path();
		for (int[] box : boxes) {
			p.rewind();
			GeneralRectangleAndroid.createPath(p, box, mercator);
			canvas.drawPath(p, paint);
		}

		/* DEBUG: render text path */
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(2);
		paint.setColor(Color.GREEN);
		// canvas.drawPath(path, paint);
	}

	@Override
	protected void drawStringForDebugging(Linestring string)
	{
		int lw = 4;
		int ps = 6;

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(lw);

		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.RED);
		Path path = GeometryConversion.createPath(string, false, mercator);
		canvas.drawPath(path, paint);

		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLUE);
		float x = mercator.getX(string.x[0]);
		float y = mercator.getY(string.y[0]);
		canvas.drawRect(
				new RectF(x - ps / 2, y - ps / 2, x + ps / 2, y + ps / 2),
				paint);

		paint.setColor(Color.MAGENTA);
		x = mercator.getX(string.x[string.x.length - 1]);
		y = mercator.getY(string.y[string.x.length - 1]);
		canvas.drawRect(
				new RectF(x - ps / 2, y - ps / 2, x + ps / 2, y + ps / 2),
				paint);

		paint.setColor(Color.GREEN);
		for (int i = 1; i < string.x.length - 1; i++) {
			x = mercator.getX(string.x[i]);
			y = mercator.getY(string.y[i]);
			canvas.drawRect(
					new RectF(x - ps / 2, y - ps / 2, x + ps / 2, y + ps / 2),
					paint);
		}
	}

}
