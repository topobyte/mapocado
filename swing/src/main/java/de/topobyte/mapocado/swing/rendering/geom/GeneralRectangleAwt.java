package de.topobyte.mapocado.swing.rendering.geom;

import java.awt.geom.Path2D;

import de.topobyte.mapocado.mapformat.CoordinateTransformer;

public class GeneralRectangleAwt
{
	public static Path2D createPath(int[] box, CoordinateTransformer mercator)
	{
		Path2D path = new Path2D.Float();
		float x0 = mercator.getX(box[0]);
		float y0 = mercator.getY(box[1]);
		path.moveTo(x0, y0);

		int k = 2;
		for (int i = 1; i < 4; i++) {
			float x = mercator.getX(box[k++]);
			float y = mercator.getY(box[k++]);
			path.lineTo(x, y);
		}
		path.lineTo(x0, y0);
		return path;
	}

}
