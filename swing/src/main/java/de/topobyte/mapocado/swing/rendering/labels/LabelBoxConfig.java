package de.topobyte.mapocado.swing.rendering.labels;

public class LabelBoxConfig
{

	int textSize;
	int border;

	public int height;
	public int lowExtra;

	public LabelBoxConfig(int textSize, int border)
	{
		this.textSize = textSize;
		this.border = border;
		lowExtra = (int) Math.ceil(0.2 * textSize);
		height = textSize + lowExtra + 2 * border;
	}

}
