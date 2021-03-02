package de.topobyte.mapocado.styles.convert;

import de.topobyte.chromaticity.ColorCode;

public interface ColorConverter
{
	public ColorCode convert(ConversionContext context, ColorCode color);
}
