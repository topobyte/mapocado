package de.topobyte.mapocado.styles.classes.element;

public class AbstractElement implements RenderElement
{

	private static final long serialVersionUID = -387100348217769334L;

	private final int level;

	public AbstractElement(int level)
	{
		this.level = level;
	}

	@Override
	public int getLevel()
	{
		return level;
	}

}
