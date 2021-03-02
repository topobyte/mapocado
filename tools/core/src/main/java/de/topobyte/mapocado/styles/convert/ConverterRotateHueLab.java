package de.topobyte.mapocado.styles.convert;

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.CieLabUtil;
import de.topobyte.mapocado.color.util.HSLColor;
import de.topobyte.mapocado.swing.rendering.Conversion;

public class ConverterRotateHueLab implements ColorConverter
{

	private float angle;

	public ConverterRotateHueLab(float angle)
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

		// get original cieLab l*
		Color c = Conversion.getColor(color);
		float ciel = CieLabUtil.getLuminance(c);

		// rotate
		HSLColor hslColor = new HSLColor(value);
		float hue = hslColor.getHue();
		Color newColor = hslColor.adjustHue(hue + angle);

		// correct l*
		Color adjusted = CieLabUtil.fixedLuminance(newColor, ciel);

		int rgb = adjusted.getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
