package de.topobyte.mapocado.styles.convert;

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.CieLabUtil;
import de.topobyte.mapocado.swing.rendering.Conversion;

public class ConverterInvertLuminanceLab implements ColorConverter
{

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int value = color.getValue();
		// extract alpha
		int alpha = value & 0xFF000000;

		// get original cieLab l*
		Color c = Conversion.getColor(color);
		float ciel = CieLabUtil.getLuminance(c);

		// correct l*
		Color adjusted = CieLabUtil.fixedLuminance(c, 1 - ciel);

		int rgb = adjusted.getRGB();

		// apply old original alpha
		rgb = (rgb & 0xFFFFFF) | alpha;
		return new ColorCode(rgb, true);
	}
}
