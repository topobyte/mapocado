package de.topobyte.mapocado.styles.classes.element;

public class BvgSymbol extends Symbol
{

	private static final long serialVersionUID = 4478366540819100451L;

	private float height;

	public BvgSymbol(int level, String source, float height)
	{
		super(level, source);
		this.height = height;
	}

	public float getHeight()
	{
		return height;
	}

}