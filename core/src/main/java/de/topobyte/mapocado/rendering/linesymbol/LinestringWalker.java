package de.topobyte.mapocado.rendering.linesymbol;

import de.topobyte.mapocado.mapformat.geom.Linestring;

public class LinestringWalker
{

	private Linestring string;
	private int n;

	private double x;
	private double y;

	private int pos = 0;

	// current segment
	private int cX1, cX2, cY1, cY2;
	private int cW, cH;
	private double cLen;
	// position on current segment
	private double cRel;
	private double cAbs;

	public LinestringWalker(Linestring string)
	{
		this.string = string;
		n = string.getNumberOfCoordinates();
		init();
		x = cX1;
		y = cY1;
	}

	private void init()
	{
		cX1 = string.x[pos];
		cX2 = string.x[pos + 1];
		cY1 = string.y[pos];
		cY2 = string.y[pos + 1];
		cW = cX2 - cX1;
		cH = cY2 - cY1;
		cLen = dist();
		cRel = 0;
		cAbs = 0;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	private double dist()
	{
		double dx = cX2 - cX1;
		double dy = cY2 - cY1;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public boolean walk(double distance)
	{
		if (cLen - cAbs > distance) {
			cAbs += distance;
			cRel = cAbs / cLen;
			x = cX1 + cRel * cW;
			y = cY1 + cRel * cH;
			return true;
		} else {
			while (true) {
				distance -= cLen - cAbs;
				if (++pos >= n - 1) {
					return false;
				}
				init();
				if (cLen - cAbs > distance) {
					cAbs += distance;
					cRel = cAbs / cLen;
					x = cX1 + cRel * cW;
					y = cY1 + cRel * cH;
					return true;
				}
			}
		}
	}

}
