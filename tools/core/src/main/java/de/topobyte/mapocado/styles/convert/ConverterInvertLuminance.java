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

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.HSLColor;

public class ConverterInvertLuminance implements ColorConverter
{

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int value = color.getValue();
		// extract alpha
		int alpha = value & 0xFF000000;

		HSLColor hslColor = new HSLColor(value);
		float luminance = hslColor.getLuminance();
		Color newColor = hslColor.adjustLuminance(100 - luminance);

		int rgb = newColor.getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
