package de.topobyte.mapocado.styles.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.topobyte.chromaticity.ColorCode;

public class ColorParser
{
	private static Pattern patternColor = Pattern
			.compile("#([0-9a-fA-F]{6}|[0-9a-fA-F]{8})");

	public static boolean isValidColor(String val)
	{
		Matcher matcher = patternColor.matcher(val);
		return matcher.matches();
	}

	public static ColorCode parseColor(String val, ColorCode defaultValue)
	{
		if (val == null) {
			return defaultValue;
		}
		Matcher matcher = patternColor.matcher(val);
		if (!matcher.matches()) {
			System.out.println(val);
		}
		if (val.length() == 7) {
			String r = val.substring(1, 3);
			String g = val.substring(3, 5);
			String b = val.substring(5, 7);
			int ir = Integer.parseInt(r, 16);
			int ig = Integer.parseInt(g, 16);
			int ib = Integer.parseInt(b, 16);
			return new ColorCode(ir, ig, ib);
		} else if (val.length() == 9) {
			String a = val.substring(1, 3);
			String r = val.substring(3, 5);
			String g = val.substring(5, 7);
			String b = val.substring(7, 9);
			int ia = Integer.parseInt(a, 16);
			int ir = Integer.parseInt(r, 16);
			int ig = Integer.parseInt(g, 16);
			int ib = Integer.parseInt(b, 16);
			return new ColorCode(ir, ig, ib, ia);
		}
		return null;
	}

	public static String colorString(ColorCode code)
	{
		if (code.getAlpha() == 255) {
			return String.format("%06x", code.getValue() & 0xFFFFFF);
		}
		return String.format("%08x", code.getValue());
	}
}
