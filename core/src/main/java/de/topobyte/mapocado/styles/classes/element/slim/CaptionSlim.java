package de.topobyte.mapocado.styles.classes.element.slim;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.classes.element.AbstractElement;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class CaptionSlim extends AbstractElement
{

	private static final long serialVersionUID = -7947703134636824841L;
	private String tag;
	private final int valK;
	private final boolean hasDeltaY;
	private final float dy;
	private final float fontSize;
	private final float strokeWidth;
	private final ColorCode fill;
	private final ColorCode stroke;
	private final FontFamily fontFamily;
	private final FontStyle fontStyle;

	public CaptionSlim(int level, String tag, int valK, boolean hasDeltaY,
			float dy, float fontSize, float strokeWidth, ColorCode fill,
			ColorCode stroke, FontFamily fontFamily, FontStyle fontStyle)
	{
		// NOTE: we store hasDeltaY here because it will later help us
		// distinguish whether dy has just a default value or it has been
		// explicitly set.
		super(level);
		this.tag = tag;
		this.valK = valK;
		this.hasDeltaY = hasDeltaY;
		this.dy = dy;
		this.fontSize = fontSize;
		this.strokeWidth = strokeWidth;
		this.fill = fill;
		this.stroke = stroke;
		this.fontFamily = fontFamily;
		this.fontStyle = fontStyle;
	}

	public String getTag()
	{
		return tag;
	}

	public int getValK()
	{
		return valK;
	}

	public boolean hasDeltaY()
	{
		return hasDeltaY;
	}

	public float getDy()
	{
		return dy;
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
