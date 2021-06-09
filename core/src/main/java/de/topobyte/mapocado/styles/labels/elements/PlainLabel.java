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

package de.topobyte.mapocado.styles.labels.elements;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class PlainLabel implements Label
{

	private FontFamily family = null;
	private FontStyle style = null;
	private float fontSize = 0;
	private float strokeWidth = 0;
	private ColorCode bg = null;
	private ColorCode fg = null;

	public PlainLabel()
	{
		// empty
	}

	public PlainLabel(PlainLabel label)
	{
		family = label.family;
		style = label.style;
		fontSize = label.fontSize;
		strokeWidth = label.strokeWidth;
		bg = label.bg;
		fg = label.fg;
	}

	public FontFamily getFamily()
	{
		return family;
	}

	public void setFamily(FontFamily family)
	{
		this.family = family;
	}

	public FontStyle getStyle()
	{
		return style;
	}

	public void setStyle(FontStyle style)
	{
		this.style = style;
	}

	public float getFontSize()
	{
		return fontSize;
	}

	public void setFontSize(float fontSize)
	{
		this.fontSize = fontSize;
	}

	public float getStrokeWidth()
	{
		return strokeWidth;
	}

	public void setStrokeWidth(float strokeWidth)
	{
		this.strokeWidth = strokeWidth;
	}

	public ColorCode getBg()
	{
		return bg;
	}

	public void setBg(ColorCode bg)
	{
		this.bg = bg;
	}

	public ColorCode getFg()
	{
		return fg;
	}

	public void setFg(ColorCode fg)
	{
		this.fg = fg;
	}

}
