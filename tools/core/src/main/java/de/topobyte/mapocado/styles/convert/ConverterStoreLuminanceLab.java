package de.topobyte.mapocado.styles.convert;

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.CieLabUtil;
import de.topobyte.mapocado.swing.rendering.Conversion;

public class ConverterStoreLuminanceLab implements ColorConverter
{

	private String storageKey;

	public ConverterStoreLuminanceLab(String storageKey)
	{
		this.storageKey = storageKey;
	}

	public String getStorageKey()
	{
		return storageKey;
	}

	public void setStorageKey(String storageKey)
	{
		this.storageKey = storageKey;
	}

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		Color c = Conversion.getColor(color);

		float ciel = CieLabUtil.getLuminance(c);
		context.store(storageKey, ciel);

		return color;
	}
}
