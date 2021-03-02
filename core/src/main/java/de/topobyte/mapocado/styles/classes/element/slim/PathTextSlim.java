package de.topobyte.mapocado.styles.classes.element.slim;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.classes.element.AbstractElement;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class PathTextSlim extends AbstractElement
{

	private static final long serialVersionUID = -7947703134636824841L;
	private final int valK;
	private final float fontSize;
	private final float strokeWidth;
	private final ColorCode fill;
	private final ColorCode stroke;
	private final FontFamily fontFamily;
	private final FontStyle fontStyle;

	public PathTextSlim(int level, int valK, float fontSize, float strokeWidth,
			ColorCode fill, ColorCode stroke, FontFamily fontFamily,
			FontStyle fontStyle)
	{
		super(level);
		this.valK = valK;
		this.fontSize = fontSize;
		this.strokeWidth = strokeWidth;
		this.fill = fill;
		this.stroke = stroke;
		this.fontFamily = fontFamily;
		this.fontStyle = fontStyle;
	}

	public int getValK()
	{
		return valK;
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
