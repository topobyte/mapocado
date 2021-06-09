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

package de.topobyte.mapocado.styles.misc;

public class PrimitiveParser
{
	public static float parseFloat(String val, float defaultValue)
	{
		if (val == null) {
			return defaultValue;
		}
		try {
			return Float.parseFloat(val);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int parseInt(String val, int defaultValue)
	{
		if (val == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static boolean parseBoolean(String val, boolean defaultValue)
	{
		if (val == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(val);
	}
}
