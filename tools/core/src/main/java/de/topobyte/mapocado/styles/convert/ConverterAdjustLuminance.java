package de.topobyte.mapocado.styles.convert;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.HSLColor;

public class ConverterAdjustLuminance implements ColorConverter
{

	private float scale;

	public ConverterAdjustLuminance(float scale)
	{
		this.scale = scale;
	}

	public float getScale()
	{
		return scale;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
	}

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int value = color.getValue();
		// extract alpha
		int alpha = value & 0xFF000000;

		HSLColor hslColor = new HSLColor(value);
		float luminance = hslColor.getLuminance();
		float newLuminance = luminance * scale;
		newLuminance = newLuminance > 100 ? 100 : newLuminance;
		HSLColor newColor = new HSLColor(hslColor.getHue(),
				hslColor.getSaturation(), newLuminance);

		int rgb = newColor.getRGB().getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
