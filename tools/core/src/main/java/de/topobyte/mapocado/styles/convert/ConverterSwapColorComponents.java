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

package de.topobyte.mapocado.styles.convert;

import de.topobyte.chromaticity.ColorCode;

public class ConverterSwapColorComponents implements ColorConverter
{

	private ColorComponentSwap swap;

	public ConverterSwapColorComponents(ColorComponentSwap swap)
	{
		this.swap = swap;
	}

	public void set(ColorComponentSwap swap)
	{
		this.swap = swap;
	}

	public ColorComponentSwap get()
	{
		return swap;
	}

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int a = color.getAlpha();
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		int t;
		switch (swap) {
		case SwapGB:
			t = g;
			g = b;
			b = t;
			break;
		case SwapRB:
			t = r;
			r = b;
			b = t;
			break;
		case SwapRG:
			t = r;
			r = g;
			g = t;
			break;
		}

		return new ColorCode(r, g, b, a);
	}
}
