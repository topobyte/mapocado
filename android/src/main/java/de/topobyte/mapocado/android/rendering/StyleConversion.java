// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

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
