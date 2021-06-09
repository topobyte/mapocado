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

public class HCL
{
	public static CieLab createLab(double c, double s, double l)
	{
		// l luminance
		// s saturation
		// c chroma

		c /= 360.0;
		double TAU = 6.283185307179586476925287;
		double L = l * 0.61 + 0.09; // L of L*a*b*
		double angle = TAU / 6.0 - c * TAU;
		double r = (l * 0.311 + 0.125) * s; // ~chroma
		double a = Math.sin(angle) * r;
		double b = Math.cos(angle) * r;
		return new CieLab(a, b, L);
	}
}
