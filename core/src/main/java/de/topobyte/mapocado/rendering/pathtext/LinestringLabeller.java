package de.topobyte.mapocado.rendering.pathtext;

import de.topobyte.mapocado.mapformat.geom.Linestring;

public class LinestringLabeller
{

	private Linestring string;
	// number of points
	private int n;
	// The lengths of the segments
	private double[] lengths;
	// Information about the angles between segments, not really the angle but
	// rather (1 + cos(alpha)) / 2, which is in range [1..0..1] for angles
	// [0..180..360]
	private double[] angles;
	private double totalLength;
	private double totalCurvature;

	public LinestringLabeller(Linestring string)
	{
		this.string = string;
		n = string.getNumberOfCoordinates();
		lengths = lengths();
		angles = angles();
		totalLength = totalLength();
		totalCurvature = totalCurvature();
	}

	public double getTotalLength()
	{
		return totalLength;
	}

	public double getTotalCurvature()
	{
		return totalCurvature;
	}

	private double[] lengths()
	{
		double[] lengths = new double[n - 1];

		int lastX = 0, lastY = 0;
		int thisX = 0, thisY = 0;

		lastX = string.x[0];
		lastY = string.y[0];

		for (int i = 1; i < n; i++) {
			thisX = string.x[i];
			thisY = string.y[i];

			int dx = thisX - lastX;
			int dy = thisY - lastY;
			lengths[i - 1] = Math.sqrt(dx * dx + dy * dy);

			lastX = thisX;
			lastY = thisY;
		}

		return lengths;
	}

	private double[] angles()
	{
		double[] angles = new double[n - 2];

		// a -- b -- c

		for (int i = 2; i < n; i++) {
			double ab = lengths[i - 2];
			double bc = lengths[i - 1];

			int aX = string.x[i - 2];
			int aY = string.y[i - 2];
			int cX = string.x[i];
			int cY = string.y[i];

			int cax = cX - aX;
			int cay = cY - aY;
			double ca = Math.sqrt(cax * cax + cay * cay);

			double cos = (ab * ab + bc * bc - ca * ca) / (2 * ab * bc);
			angles[i - 2] = (1 + cos) / 2;

			ab = bc;
		}
		return angles;
	}

	private double totalLength()
	{
		double total = 0;
		for (int i = 0; i < n - 1; i++) {
			total += lengths[i];
		}
		return total;
	}

	private double totalCurvature()
	{
		double total = 0;
		for (int i = 0; i < n - 2; i++) {
			total += angles[i];
		}
		return total;
	}

	public double getCurvature(double offset, double length)
	{
		double end = offset + length;
		double done = 0;
		int indexStart = 0, indexEnd = 0;
		int i;
		for (i = 0; i < n; i++) {
			if (done + lengths[i] >= offset) {
				indexStart = i;
				break;
			}
			done += lengths[i];
		}
		for (; i < n; i++) {
			if (done + lengths[i] >= end) {
				indexEnd = i;
				break;
			}
			done += lengths[i];
		}
		// System.out.print("lengths: ");
		// for (int k = 0; k < lengths.length; k++) {
		// System.out.print(String.format("%.2f", lengths[k]) + ", ");
		// }
		// System.out.println();
		// System.out.print("angles: ");
		// for (int k = 0; k < angles.length; k++) {
		// System.out.print(String.format("%.2f", angles[k]) + ", ");
		// }
		// System.out.println();
		// System.out.println("offset: " + offset);
		// System.out.println(indexStart + "," + indexEnd);
		double c = 0;
		for (int k = indexStart; k < indexEnd; k++) {
			c += angles[k];
		}
		return c;
	}

	// This method tries all substrings with
	public double optimize(double textLength, double acceptableCurvature)
	{
		// System.out.println("optimize with length: " + textLength);
		if (totalLength < textLength) {
			return -1;
		}

		// Initialization. Determine initial segments whose cumulated lengths
		// cover the textLength.
		int i = 0;
		double len = 0;
		for (i = 0; i < n - 1; i++) {
			len += lengths[i];
			if (len >= textLength) {
				break;
			}
		}
		double curvature = 0;
		for (int k = 0; k < i; k++) {
			curvature += angles[k];
		}

		// Segments a through b are the current segments
		int a = 0;
		int b = i;

		// Best values so far
		boolean found = false;
		double bestScore = Double.MAX_VALUE;
		int bestA = 0;
		int bestB = 0;
		double bestLen = 0;

		// Now loop. In each iteration move the list one element forward. Then,
		// add as many segments as necessary at the end of the list.
		while (true) {
			boolean acceptable = curvature <= acceptableCurvature;
			// System.out.println(String.format(
			// "%b %d - %d: length: %.2f, curvature: %.8f", acceptable, a,
			// b, len, curvature));
			double score = curvature;
			if (acceptable && score < bestScore) {
				found = true;
				bestA = a;
				bestB = b;
				bestScore = score;
				bestLen = len;
			}

			// Remove front segment
			len -= lengths[a];
			if (a < b) {
				curvature -= angles[a];
			}
			a = a + 1;

			boolean empty = a > b;

			// If necessary, append segments
			if (len >= textLength) {
				// Still long enough, continue
				continue;
			}

			for (i = b + 1; i < n - 1; i++) {
				len += lengths[i];
				if (len >= textLength) {
					break;
				}
			}
			if (len < textLength) {
				// No segments left, break
				break;
			}
			for (int k = empty ? b + 1 : b; k < i; k++) {
				curvature += angles[k];
			}
			b = i;
		}

		if (!found) {
			return -1;
		}
		double offset = (bestLen - textLength) / 2;
		// System.out.println("starting at segment " + bestA);
		// System.out.println("initial offset: " + offset);
		for (int k = 0; k < bestA; k++) {
			offset += lengths[k];
		}
		return offset;
	}
}
