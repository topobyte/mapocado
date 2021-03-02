package de.topobyte.mapocado.styles.convert;

import de.topobyte.chromaticity.ColorCode;

public class ConverterIdentity implements ColorConverter
{

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int value = color.getValue();
		return new ColorCode(value, true);
	}
}
