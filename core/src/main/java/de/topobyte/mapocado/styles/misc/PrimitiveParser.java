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
