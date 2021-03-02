package de.topobyte.mapocado.styles.convert;

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.HSLColor;

public class ConverterRotateHue implements ColorConverter
{

	private float angle;

	public ConverterRotateHue(float angle)
	{
		this.angle = angle;
	}

	public float getAngle()
	{
		return angle;
	}

	public void setAngle(float angle)
	{
		this.angle = angle;
	}

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int value = color.getValue();
		// extract alpha
		int alpha = value & 0xFF000000;

		HSLColor hslColor = new HSLColor(value);
		float hue = hslColor.getHue();
		Color newColor = hslColor.adjustHue(hue + angle);

		int rgb = newColor.getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
