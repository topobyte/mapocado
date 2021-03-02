package de.topobyte.mapocado.rendering.linesymbol;

import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.mapformat.geom.Linestring;

public abstract class LineSymbolRenderer<T>
{
	private LengthTransformer transformer;
	protected T symbol;
	protected float height;
	private double widthStorage;
	private double offsetStorage;
	protected float combinedScaleFactor;

	public LineSymbolRenderer(T symbol)
	{
		this.symbol = symbol;
	}

	protected void init(LengthTransformer transformer, float height,
			double widthStorage, double offsetStorage,
			float combinedScaleFactor)
	{
		this.transformer = transformer;
		this.height = height;
		this.widthStorage = widthStorage * combinedScaleFactor;
		this.offsetStorage = offsetStorage * combinedScaleFactor;
		this.combinedScaleFactor = combinedScaleFactor;
	}

	// Render a symbol on a way without repeating it
	public void renderLineSymbol(Linestring string)
	{
		LinestringWalker walker = new LinestringWalker(string);

		if (!walker.walk(offsetStorage)) {
			return;
		}
		double x1 = walker.getX();
		double y1 = walker.getY();
		if (!walker.walk(widthStorage)) {
			return;
		}
		double x2 = walker.getX();
		double y2 = walker.getY();
		float x = transformer.getX((int) Math.round(x1));
		float y = transformer.getY((int) Math.round(y1));
		double cos = (x2 - x1) / widthStorage;
		double sin = (y2 - y1) / widthStorage;
		renderLineSymbol(x, y, sin, cos);
	}

	// Render a symbol on a way and repeat it after 'repeatStorage' gap
	public void renderLineSymbol(Linestring string, double repeatStorage)
	{
		LinestringWalker walker = new LinestringWalker(string);

		if (!walker.walk(offsetStorage)) {
			return;
		}

		double scaledRepeat = repeatStorage * combinedScaleFactor;

		while (true) {
			double x1 = walker.getX();
			double y1 = walker.getY();
			if (!walker.walk(widthStorage)) {
				break;
			}
			double x2 = walker.getX();
			double y2 = walker.getY();
			float x = transformer.getX((int) Math.round(x1));
			float y = transformer.getY((int) Math.round(y1));
			double cos = (x2 - x1) / widthStorage;
			double sin = (y2 - y1) / widthStorage;
			renderLineSymbol(x, y, sin, cos);
			if (!walker.walk(scaledRepeat)) {
				break;
			}
		}
	}

	// Render the symbol at a given position and with given angle
	protected abstract void renderLineSymbol(float x, float y, double sin,
			double cos);
}
