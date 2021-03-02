package de.topobyte.mapocado.styles.convert;

import java.util.List;

import de.topobyte.chromaticity.ColorCode;

public class ChainedConverter implements ColorConverter
{

	private final List<ColorConverter> converters;

	public ChainedConverter(List<ColorConverter> converters)
	{
		this.converters = converters;

	}

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		ColorCode mod = color;
		for (ColorConverter converter : converters) {
			mod = converter.convert(context, mod);
		}
		return mod;
	}

}
