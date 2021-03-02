package de.topobyte.mapocado.android.rendering.gather;

import de.topobyte.mapocado.mapformat.geom.Linestring;

public class PathTextElement
{

	public final Linestring string;
	public final String text;

	public PathTextElement(Linestring string, String text)
	{
		this.string = string;
		this.text = text;
	}

}
