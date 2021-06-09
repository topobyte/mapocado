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

package de.topobyte.misc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class TimeUtil
{

	/**
	 * compute the difference in seconds between two timestamps.
	 * 
	 * @param time1
	 *            the first timestamp.
	 * @param time2
	 *            the seconds timestamp.
	 * @return the difference in seconds.
	 */
	public static double timeDiff(long time1, long time2)
	{
		long diff = time2 - time1;
		return diff / 1000.0;
	}

	/*
	 * Utility: time measuring
	 */

	// here we store times
	private static Map<String, Long> times = new HashMap<>();

	// associate the current time with the identifier 'key'
	public static void time(String key)
	{
		times.put(key, System.currentTimeMillis());
	}

	// print the time passed in milliseconds since you called time(key).
	public static void time(String key, String message)
	{
		long stop = System.currentTimeMillis();
		long start = times.get(key);
		long interval = stop - start;
		System.out.println(String.format(message, interval));
	}

}
