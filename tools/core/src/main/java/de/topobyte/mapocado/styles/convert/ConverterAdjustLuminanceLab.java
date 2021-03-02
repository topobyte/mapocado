package de.topobyte.mapocado.styles.convert;

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.CieLabUtil;
import de.topobyte.mapocado.swing.rendering.Conversion;

public class ConverterAdjustLuminanceLab implements ColorConverter
{

	private float scale;

	public ConverterAdjustLuminanceLab(float scale)
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

		// get original cieLab l*
		Color c = Conversion.getColor(color);
		float ciel = CieLabUtil.getLuminance(c);
		float newCiel = ciel * scale;
		if (newCiel > 1) {
			newCiel = 1;
		}

		// correct l*
		Color adjusted = CieLabUtil.fixedLuminance(c, newCiel);

		int rgb = adjusted.getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
