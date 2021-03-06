// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.mapocado.color.util;

import java.awt.color.ColorSpace;

public class CieLabColorSpace extends ColorSpace
{

	public static CieLabColorSpace getInstance()
	{
		return Holder.INSTANCE;
	}

	@Override
	public float[] fromCIEXYZ(float[] colorvalue)
	{
		double l = f(colorvalue[1]); // Y1
		double L = 116.0 * l - 16.0;
		double a = 500.0 * (f(colorvalue[0]) - l);
		double b = 200.0 * (l - f(colorvalue[2]));
		return new float[] { (float) L, (float) a, (float) b };
	}

	@Override
	public float[] fromRGB(float[] rgbvalue)
	{
		float[] xyz = CIEXYZ.fromRGB(rgbvalue);
		return fromCIEXYZ(xyz);
	}

	@Override
	public float getMaxValue(int component)
	{
		return 128f;
	}

	@Override
	public float getMinValue(int component)
	{
		return (component == 0) ? 0f : -128f;
	}

	@Override
	public String getName(int idx)
	{
		return String.valueOf("Lab".charAt(idx));
	}

	@Override
	public float[] toCIEXYZ(float[] colorvalue)
	{
		double i = (colorvalue[0] + 16.0) * (1.0 / 116.0);
		double X = fInv(i + colorvalue[1] * (1.0 / 500.0));
		double Y = fInv(i);
		double Z = fInv(i - colorvalue[2] * (1.0 / 200.0));
		return new float[] { (float) X, (float) Y, (float) Z };
	}

	@Override
	public float[] toRGB(float[] colorvalue)
	{
		float[] xyz = toCIEXYZ(colorvalue);
		return CIEXYZ.toRGB(xyz);
	}

	CieLabColorSpace()
	{
		super(ColorSpace.TYPE_Lab, 3);
	}

	private static double f(double x)
	{
		if (x > 216.0 / 24389.0) {
			return Math.cbrt(x);
		} else {
			return (841.0 / 108.0) * x + N;
		}
	}

	private static double fInv(double x)
	{
		if (x > 6.0 / 29.0) {
			return x * x * x;
		} else {
			return (108.0 / 841.0) * (x - N);
		}
	}

	private static class Holder
	{
		static final CieLabColorSpace INSTANCE = new CieLabColorSpace();
	}

	private static final long serialVersionUID = 5027741380892134289L;

	private static final ColorSpace CIEXYZ = ColorSpace
			.getInstance(ColorSpace.CS_CIEXYZ);

	private static final double N = 4.0 / 29.0;

}