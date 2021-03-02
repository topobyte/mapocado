package de.topobyte.mapocado.styles.labels.elements;

import de.topobyte.chromaticity.ColorCode;

public class DotLabel extends PlainLabel
{

	private ColorCode dotFg = null;
	private float radius = 0;

	public DotLabel()
	{
		super();
	}

	public DotLabel(PlainLabel label)
	{
		super(label);
	}

	public DotLabel(DotLabel label)
	{
		this((PlainLabel) label);
		dotFg = label.dotFg;
		radius = label.radius;
	}

	public ColorCode getDotFg()
	{
		return dotFg;
	}

	public void setDotFg(ColorCode dotFg)
	{
		this.dotFg = dotFg;
	}

	public float getRadius()
	{
		return radius;
	}

	public void setRadius(float radius)
	{
		this.radius = radius;
	}

}
