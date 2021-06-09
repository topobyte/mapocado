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

package de.topobyte.mapocado.swing.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.misc.enums.CapType;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class Conversion
{

	public static String getFontFamily(FontFamily fontFamily)
	{
		switch (fontFamily) {
		case MONOSPACE:
			return Font.MONOSPACED;
		case SANS_SERIF:
			return Font.SANS_SERIF;
		case SERIF:
			return Font.SERIF;
		}
		return Font.SANS_SERIF;
	}

	public static int getFontStyle(FontStyle fontStyle)
	{
		int style = Font.PLAIN;
		switch (fontStyle) {
		case BOLD:
			style = Font.BOLD;
			break;
		case BOLD_ITALIC:
			style = Font.BOLD | Font.ITALIC;
			break;
		case ITALIC:
			style = Font.ITALIC;
			break;
		case NORMAL:
			style = Font.PLAIN;
			break;
		}
		return style;
	}

	public static int getLineCap(CapType capType)
	{
		switch (capType) {
		case BUTT:
			return BasicStroke.CAP_BUTT;
		default:
		case ROUND:
			return BasicStroke.CAP_ROUND;
		case SQUARE:
			return BasicStroke.CAP_SQUARE;
		}
	}

	public static Color getColor(ColorCode code)
	{
		if (code == null) {
			return null;
		}
		return new Color(code.getValue(), true);
	}
}
