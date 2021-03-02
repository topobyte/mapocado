package de.topobyte.mapocado.color.util;

public class CieLab
{

	private static double xn = 0.9643;
	private static double yn = 1.00;
	private static double zn = 0.8251;

	private double l, a, b;

	public CieLab(double a, double b, double l)
	{
		this.a = a;
		this.b = b;
		this.l = l;
	}

	public CieLab(double angle, double l)
	{
		a = Math.cos(angle) * 120;
		b = Math.sin(angle) * 120;
		this.l = l * 100;
	}

	public static CieLab fromXYZ(double x, double y, double z)
	{
		double l, a, b;
		if (y / yn > 0.008856) {
			l = 116 * Math.pow(y / yn, 1.0 / 3) - 16;
		} else {
			l = 903.3 * y / yn;
		}
		a = 500 * (f(x / xn) - f(y / yn));
		b = 200 * (f(y / yn) - f(z / zn));
		return new CieLab(a, b, l);
	}

	public static CieLab fromAB(double a, double b, double l)
	{
		return new CieLab(a * 240 - 120, b * 240 - 120, l * 100);
	}

	private static double f(double t)
	{
		if (t > 0.008856) {
			return Math.pow(t, 1.0 / 3);
		} else {
			return 7.787 * t + 16 / 116;
		}
	}

	public CieXYZ toXYZ()
	{
		double p = (l + 16) / 116.0;
		double x = xn * Math.pow(p + a / 500.0, 3);
		double y = yn * Math.pow(p, 3);
		double z = zn * Math.pow(p - b / 200.0, 3);
		return new CieXYZ(x, y, z);
	}

	public double getL()
	{
		return l;
	}

	public void setL(double l)
	{
		this.l = l;
	}

	public double getA()
	{
		return a;
	}

	public void setA(double a)
	{
		this.a = a;
	}

	public double getB()
	{
		return b;
	}

	public void setB(double b)
	{
		this.b = b;
	}
}
