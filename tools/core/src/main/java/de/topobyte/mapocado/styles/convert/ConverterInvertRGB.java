package de.topobyte.mapocado.styles.convert;

import de.topobyte.chromaticity.ColorCode;

public class ConverterInvertRGB implements ColorConverter
{

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int value = color.getValue();
		// extract alpha
		int alpha = value & 0xFF000000;

		int rgb = (0xFFFFFF - (0xFFFFFF & value));

		// apply original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
