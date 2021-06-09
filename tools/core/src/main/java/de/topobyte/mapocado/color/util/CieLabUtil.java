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

import java.awt.Color;
import java.awt.color.ColorSpace;

public class CieLabUtil
{
	private static ColorSpace sRgbSpace = ColorSpace
			.getInstance(ColorSpace.CS_sRGB);
	private static ColorSpace labSpace = CieLabColorSpace.getInstance();

	// return value in [0..1]
	public static float getLuminance(Color c)
	{
		float[] rgb = new float[] { c.getRed() / 255f, c.getGreen() / 255f,
				c.getBlue() / 255f };
		float[] xyz = sRgbSpace.toCIEXYZ(rgb);
		float[] cielab = labSpace.fromCIEXYZ(xyz);
		float ciel = cielab[0] / 100f;
		return ciel;
	}

	// k wihtin [0..1]
	public static Color fixedLuminance(Color color, float k)
	{
		int steps = 1024;
		int low = 0;
		int high = steps;

		Color result = color;
		float best = Float.MAX_VALUE;
		float[] hsl = HSLColor.fromRGB(color);
		while (Math.abs(high - low) > 1) {
			int i = (int) ((high + low) / 2f);
			Color c = HSLColor.toRGB(hsl[0], hsl[1], 100 * i / (float) steps);
			float[] rgb = new float[] { c.getRed() / 255f, c.getGreen() / 255f,
					c.getBlue() / 255f };
			float[] xyz = sRgbSpace.toCIEXYZ(rgb);
			float[] cielab = labSpace.fromCIEXYZ(xyz);
			float ciel = cielab[0] / 100f;
			float dist = k - ciel;
			float abs = Math.abs(k - ciel);
			if (abs < best) {
				best = abs;
				result = c;
			}
			if (dist > 0) {
				low = i;
			} else {
				high = i;
			}
		}

		return result;
	}

	// // k wihtin [0..1]
	// public static Color fixedLuminance(Color color, float k)
	// {
	// int steps = 256;
	// Color result = color;
	// float dist = Float.MAX_VALUE;
	// float[] hsl = HSLColor.fromRGB(color);
	// for (int i = 0; i <= steps; i++) {
	// Color c = HSLColor.toRGB(hsl[0], hsl[1], 100 * i / (float) steps);
	// float[] rgb = new float[] { c.getRed() / 255f,
	// c.getGreen() / 255f, c.getBlue() / 255f };
	// float[] xyz = sRgbSpace.toCIEXYZ(rgb);
	// float[] cielab = labSpace.fromCIEXYZ(xyz);
	// float ciel = cielab[0] / 100f;
	// float myDist = Math.abs(k - ciel);
	// if (myDist < dist) {
	// dist = myDist;
	// result = c;
	// }
	// }
	//
	// return result;
	// }
}
