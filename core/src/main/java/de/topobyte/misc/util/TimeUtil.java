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
