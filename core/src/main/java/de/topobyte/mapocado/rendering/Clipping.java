package de.topobyte.mapocado.rendering;

public class Clipping
{

	private double xmin, xmax, ymin, ymax;

	/**
	 * Create a clipping instance that may be used to clip to a tile with 256
	 * pixel square plus tolerance pixels at each side
	 */
	public Clipping(int width, int height, int tolerance)
	{
		xmin = 0 - tolerance;
		xmax = width + tolerance;
		ymin = 0 - tolerance;
		ymax = height + tolerance;
	}

	/**
	 * Create a clipping instance with arbitrary rectangular bounds.
	 */
	public Clipping(int xmin, int xmax, int ymin, int ymax)
	{
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
	}

	// bitmasks for endpoint positions

	final static int INSIDE = 0; // 0000
	final static int LEFT = 1; // 0001
	final static int RIGHT = 2; // 0010
	final static int BOTTOM = 4; // 0100
	final static int TOP = 8; // 1000

	// Compute the bit code for a point (x, y) using the clip rectangle
	// bounded diagonally by (xmin, ymin), and (xmax, ymax)
	private int ComputeOutCode(double x, double y)
	{
		int code;

		code = INSIDE; // initialised as being inside of clip window

		if (x < xmin) // to the left of clip window
			code |= LEFT;
		else if (x > xmax) // to the right of clip window
			code |= RIGHT;
		if (y < ymin) // below the clip window
			code |= BOTTOM;
		else if (y > ymax) // above the clip window
			code |= TOP;

		return code;
	}

	// Cohen-Sutherland clipping algorithm
	public boolean isNotOutside(double x0, double y0, double x1, double y1)
	{

		// compute outcodes for P0, P1, and whatever point lies outside the clip
		// rectangle
		int outcode0 = ComputeOutCode(x0, y0);
		int outcode1 = ComputeOutCode(x1, y1);
		boolean accept = false;

		while (true) {
			if ((outcode0 | outcode1) == 0) { // Bitwise OR is 0. Trivially
												// accept
												// and get out of loop
				accept = true;
				break;
			} else if ((outcode0 & outcode1) != 0) { // Bitwise AND is not 0.
														// Trivially
				// reject and get out of loop
				break;
			} else {
				// failed both tests, so calculate the line segment to clip
				// from an outside point to an intersection with clip edge
				double x, y;

				// At least one endpoint is outside the clip rectangle; pick it.
				int outcodeOut = outcode0 != 0 ? outcode0 : outcode1;

				// Now find the intersection point;
				// use formulas y = y0 + slope * (x - x0), x = x0 + (1 / slope)
				// * (y - y0)
				if ((outcodeOut & TOP) != 0) { // point is above the clip
												// rectangle
					x = x0 + (x1 - x0) * (ymax - y0) / (y1 - y0);
					y = ymax;
				} else if ((outcodeOut & BOTTOM) != 0) { // point is below the
															// clip
					// rectangle
					x = x0 + (x1 - x0) * (ymin - y0) / (y1 - y0);
					y = ymin;
				} else if ((outcodeOut & RIGHT) != 0) { // point is to the right
														// of
					// clip rectangle
					y = y0 + (y1 - y0) * (xmax - x0) / (x1 - x0);
					x = xmax;
					// } else if ((outcodeOut & LEFT) != 0) { // point is to the
					// left
				} else {
					// of clip
					// rectangle
					y = y0 + (y1 - y0) * (xmin - x0) / (x1 - x0);
					x = xmin;
				}

				// Now we move outside point to intersection point to clip
				// and get ready for next pass.
				if (outcodeOut == outcode0) {
					x0 = x;
					y0 = y;
					outcode0 = ComputeOutCode(x0, y0);
				} else {
					x1 = x;
					y1 = y;
					outcode1 = ComputeOutCode(x1, y1);
				}
			}
		}
		// line segment is: (x0, y0, x1, y1);
		return accept;
	}
}
