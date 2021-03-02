package de.topobyte.mapocado.styles.classes.element;

import de.topobyte.chromaticity.ColorCode;

public class Area extends AbstractElement
{

	private static final long serialVersionUID = 709016942331428724L;

	private final String source;
	private final ColorCode fill;
	private final ColorCode stroke;
	private final float strokeWidth;

	public Area(int level, String source, ColorCode fill, ColorCode stroke,
			float strokeWidth)
	{
		super(level);
		this.source = source;
		this.fill = fill;
		this.stroke = stroke;
		this.strokeWidth = strokeWidth;
	}

	public String getSource()
	{
		return source;
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
