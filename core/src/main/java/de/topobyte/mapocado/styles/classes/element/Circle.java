package de.topobyte.mapocado.styles.classes.element;

import de.topobyte.chromaticity.ColorCode;

public class Circle extends AbstractElement
{
	private static final long serialVersionUID = 266124950099399277L;

	private final float radius;
	private final boolean scaleRadius;
	private final ColorCode fill;
	private final ColorCode stroke;
	private final float strokeWidth;

	public Circle(int level, float radius, boolean scaleRadius, ColorCode fill,
			ColorCode stroke, float strokeWidth)
	{
		super(level);
		this.radius = radius;
		this.scaleRadius = scaleRadius;
		this.fill = fill;
		this.stroke = stroke;
		this.strokeWidth = strokeWidth;

	}

	public float getRadius()
	{
		return radius;
	}

	public boolean isScaleRadius()
	{
		return scaleRadius;
	}

	public ColorCode getFill()
	{
		return fill;
	}

	public ColorCode getStroke()
	{
		return stroke;
	}

	public float getStrokeWidth()
	{
		return strokeWidth;
	}

}
