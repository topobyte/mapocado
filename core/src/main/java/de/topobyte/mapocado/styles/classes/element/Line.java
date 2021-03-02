package de.topobyte.mapocado.styles.classes.element;

import java.util.List;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.misc.enums.CapType;

public class Line extends AbstractElement
{

	private static final long serialVersionUID = 3653466892877253022L;

	private final ColorCode stroke;
	private final float strokeWidth;
	private final CapType capType;
	private final List<Float> dashArray;

	public Line(int level, ColorCode stroke, float strokeWidth, CapType capType,
			List<Float> dashArray)
	{
		super(level);
		this.stroke = stroke;
		this.strokeWidth = strokeWidth;
		this.capType = capType;
		this.dashArray = dashArray;
	}

	public ColorCode getStroke()
	{
		return stroke;
	}

	public float getStrokeWidth()
	{
		return strokeWidth;
	}

	public CapType getCapType()
	{
		return capType;
	}

	public List<Float> getDashArray()
	{
		return dashArray;
	}
}
