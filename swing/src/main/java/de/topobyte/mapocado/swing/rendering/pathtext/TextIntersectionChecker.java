package de.topobyte.mapocado.swing.rendering.pathtext;

import java.awt.geom.Area;

public class TextIntersectionChecker
{

	Area area = new Area();

	public void add(Area textBox)
	{
		area.add(textBox);
	}

	public boolean isValid(Area textBox)
	{
		Area test = new Area();
		test.add(area);
		test.intersect(textBox);
		return test.isEmpty();
	}

}
