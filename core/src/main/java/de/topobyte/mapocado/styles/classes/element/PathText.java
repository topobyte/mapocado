package de.topobyte.mapocado.styles.classes.element;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class PathText extends AbstractElement
{

	private static final long serialVersionUID = -7947703134636824841L;
	private final String key;
	private final float fontSize;
	private final float strokeWidth;
	private final ColorCode fill;
	private final ColorCode stroke;
	private final FontFamily fontFamily;
	private final FontStyle fontStyle;

	public PathText(int level, String key, float fontSize, float strokeWidth,
			ColorCode fill, ColorCode stroke, FontFamily fontFamily,
			FontStyle fontStyle)
	{
		super(level + 1000);
		this.key = key;
		this.fontSize = fontSize;
		this.strokeWidth = strokeWidth;
		this.fill = fill;
		this.stroke = stroke;
		this.fontFamily = fontFamily;
		this.fontStyle = fontStyle;
	}

	public String getKey()
	{
		return key;
	}

	public float getFontSize()
	{
		return fontSize;
	}

	public float getStrokeWidth()
	{
		return strokeWidth;
	}

	public ColorCode getFill()
	{
		return fill;
	}

	public ColorCode getStroke()
	{
		return stroke;
	}

	public FontFamily getFontFamily()
	{
		return fontFamily;
	}

	public FontStyle getFontStyle()
	{
		return fontStyle;
	}
}
