package de.topobyte.mapocado.android.rendering.geom;

import android.graphics.Path;
import de.topobyte.mapocado.mapformat.CoordinateTransformer;

public class GeneralRectangleAndroid
{
	public static void createPath(Path path, int[] box,
			CoordinateTransformer mercator)
	{
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
	}

}
