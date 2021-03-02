package de.topobyte.mapocado.swing.rendering.pathtext;

import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;

public class TextStroke implements Stroke
{
	private String text;
	private Font font;

	public TextStroke(String text, Font font)
	{
		this.text = text;
		this.font = font;
	}

	@Override
	public Shape createStrokedShape(Shape shape)
	{
		Shape result = AwtTextUtil.createStrokedShape(shape, font, text);
		return result;
	}
}
