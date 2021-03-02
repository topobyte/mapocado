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
