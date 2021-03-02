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
