package de.topobyte.mapocado.styles.convert;

import de.topobyte.chromaticity.ColorCode;

public class ConverterSwapColorComponents implements ColorConverter
{

	private ColorComponentSwap swap;

	public ConverterSwapColorComponents(ColorComponentSwap swap)
	{
		this.swap = swap;
	}

	public void set(ColorComponentSwap swap)
	{
		this.swap = swap;
	}

	public ColorComponentSwap get()
	{
		return swap;
	}

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		int a = color.getAlpha();
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		int t;
		switch (swap) {
		case SwapGB:
			t = g;
			g = b;
			b = t;
			break;
		case SwapRB:
			t = r;
			r = b;
			b = t;
			break;
		case SwapRG:
			t = r;
			r = g;
			g = t;
			break;
		}

		return new ColorCode(r, g, b, a);
	}
}
