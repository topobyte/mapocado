package de.topobyte.mapocado.styles.convert;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.HSLColor;

public class ConverterAdjustSaturation implements ColorConverter
{

	private float scale;

	public ConverterAdjustSaturation(float scale)
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
		float saturation = hslColor.getSaturation();
		float newSaturation = saturation * scale;
		newSaturation = newSaturation > 100 ? 100 : newSaturation;
		HSLColor newColor = new HSLColor(hslColor.getHue(), newSaturation,
				hslColor.getLuminance());

		int rgb = newColor.getRGB().getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
