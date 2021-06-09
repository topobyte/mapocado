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

import de.topobyte.chromaticity.ColorCode;

public class CieXYZ
{
	private double x, y, z;

	public CieXYZ(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public CieXYZ(int r255, int g255, int b255)
	{
		double r = r255 / 255.0;
		double g = g255 / 255.0;
		double b = b255 / 255.0;
		// [ X ] = [ 0.412453 0.357580 0.180423 ] * [ R ]
		// [ Y ] = [ 0.212671 0.715160 0.072169 ] * [ G ]
		// [ Z ] = [ 0.019334 0.119193 0.950227 ] * [ B ]
		x = 0.412453 * r + 0.357580 * g + 0.180423 * b;
		y = 0.212671 * r + 0.715160 * g + 0.072169 * b;
		z = 0.019334 * r + 0.119193 * g + 0.950227 * b;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}

	// [ R ] = [ 3.240479 -1.537150 -0.498535 ] * [ X ]
	// [ G ] = [ -0.969256 1.875992 0.041556 ] * [ Y ]
	// [ B ] = [ 0.055648 -0.204043 1.057311 ] * [ Z ]

	public ColorCode toRGB()
	{
		// double nx = x / (x+y+z);
		// double ny = y / (x+y+z);
		// double nz = z / (x+y+z);
		double nx = x;
		double ny = y;
		double nz = z;

		double r = 3.240479 * nx + -1.537150 * ny + -0.498535 * nz;
		double g = -0.969256 * nx + 1.875992 * ny + 0.041556 * nz;
		double b = 0.055648 * nx + -0.204043 * ny + 1.057311 * nz;

		// double r = 3.1338561 * nx + -1.6168667 * ny + -0.4906146 * nz;
		// double g = -0.9787684 * nx + 1.9161415 * ny + 0.0334540 * nz;
		// double b = 0.0719453 * nx + -0.2289914 * ny + 1.4052427 * nz;

		// double m = Math.max(r, Math.max(g, b));
		// r = r / m;
		// g = g / m;
		// b = b / m;

		int r255 = clamp((int) Math.round(r * 255), 0, 255);
		int g255 = clamp((int) Math.round(g * 255), 0, 255);
		int b255 = clamp((int) Math.round(b * 255), 0, 255);
		return new ColorCode(r255, g255, b255);
	}

	private int clamp(int value, int min, int max)
	{
		return value < min ? min : value > max ? max : value;
	}
}
