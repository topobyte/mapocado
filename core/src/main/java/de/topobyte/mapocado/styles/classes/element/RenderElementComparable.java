package de.topobyte.mapocado.styles.classes.element;

import java.util.Comparator;

public class RenderElementComparable implements Comparator<RenderElement>
{

	@Override
	public int compare(RenderElement o1, RenderElement o2)
	{
		return o1.getLevel() - o2.getLevel();
	}

}
