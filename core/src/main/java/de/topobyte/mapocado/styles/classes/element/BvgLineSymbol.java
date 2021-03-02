package de.topobyte.mapocado.styles.classes.element;

public class BvgLineSymbol extends LineSymbol
{

	private static final long serialVersionUID = -3280221709806087806L;

	private float width;

	public BvgLineSymbol(int level, String source, float width, float offset,
			boolean repeat, float repeatDistance)
	{
		super(level, source, offset, repeat, repeatDistance);
		this.width = width;
	}

	public float getWidth()
	{
		return width;
	}

}