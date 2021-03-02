package de.topobyte.mapocado.android.rendering;

import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Typeface;
import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.misc.enums.CapType;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class StyleConversion
{

	public static Typeface getFontFamily(FontFamily fontFamily)
	{
		switch (fontFamily) {
		default:
		case SANS_SERIF:
			return Typeface.SANS_SERIF;
		case MONOSPACE:
			return Typeface.MONOSPACE;
		case SERIF:
			return Typeface.SERIF;
		}
	}

	public static int getFontStyle(FontStyle fontStyle)
	{
		int style = Typeface.NORMAL;
		switch (fontStyle) {
		case BOLD:
			style = Typeface.BOLD;
			break;
		case BOLD_ITALIC:
			style = Typeface.BOLD | Typeface.ITALIC;
			break;
		case ITALIC:
			style = Typeface.ITALIC;
			break;
		case NORMAL:
			style = Typeface.NORMAL;
			break;
		}
		return style;
	}

	public static Cap getLineCap(CapType capType)
	{
		switch (capType) {
		case BUTT:
			return Paint.Cap.BUTT;
		default:
		case ROUND:
			return Paint.Cap.ROUND;
		case SQUARE:
			return Paint.Cap.SQUARE;
		}
	}

	public static int getColor(ColorCode code)
	{
		return code.getValue();
	}
}
