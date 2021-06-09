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

package de.topobyte.mapocado.styles.misc;

import de.topobyte.mapocado.styles.misc.enums.CapType;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class EnumParser
{
	public static FontFamily parseFontFamiliy(String val,
			FontFamily defaultFamily)
	{
		FontFamily family = defaultFamily;
		if (val != null) {
			if (val.equals("serif")) {
				family = FontFamily.SERIF;
			} else if (val.equals("monospace")) {
				family = FontFamily.MONOSPACE;
			} else if (val.equals("sans serif")) {
				family = FontFamily.SANS_SERIF;
			}
		}
		return family;
	}

	public static FontStyle parseFontStyle(String val, FontStyle defaultStyle)
	{
		FontStyle style = defaultStyle;
		if (val != null) {
			if (val.equals("bold")) {
				style = FontStyle.BOLD;
			} else if (val.equals("bold_italic")) {
				style = FontStyle.BOLD_ITALIC;
			} else if (val.equals("italic")) {
				style = FontStyle.ITALIC;
			} else if (val.equals("normal")) {
				style = FontStyle.NORMAL;
			}
		}
		return style;
	}

	public static CapType parseCapType(String val, CapType defaultType)
	{
		CapType type = defaultType;
		if (val != null) {
			if (val.equals("round")) {
				type = CapType.ROUND;
			} else if (val.equals("butt")) {
				type = CapType.BUTT;
			} else if (val.equals("square")) {
				type = CapType.SQUARE;
			}
		}
		return type;
	}
}
