package de.topobyte.mapocado.styles.convert;

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.CieLabUtil;
import de.topobyte.mapocado.swing.rendering.Conversion;

public class ConverterLoadLuminanceLab implements ColorConverter
{

	private String storageKey;

	public ConverterLoadLuminanceLab(String storageKey)
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
		float ciel = (Float) context.load(storageKey);

		int value = color.getValue();
		// extract alpha
		int alpha = value & 0xFF000000;

		Color c = Conversion.getColor(color);
		// correct l*
		Color adjusted = CieLabUtil.fixedLuminance(c, ciel);

		int rgb = adjusted.getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
