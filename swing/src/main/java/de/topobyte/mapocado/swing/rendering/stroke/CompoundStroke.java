package de.topobyte.mapocado.swing.rendering.stroke;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;

public class CompoundStroke implements Stroke
{
	public final static int ADD = 0;
	public final static int SUBTRACT = 1;
	public final static int INTERSECT = 2;
	public final static int DIFFERENCE = 3;

	private Stroke stroke1, stroke2;
	private int operation;

	public CompoundStroke(Stroke stroke1, Stroke stroke2, int operation)
	{
		this.stroke1 = stroke1;
		this.stroke2 = stroke2;
		this.operation = operation;
	}

	@Override
	public Shape createStrokedShape(Shape shape)
	{
		Area area1 = new Area(stroke1.createStrokedShape(shape));
		Area area2 = new Area(stroke2.createStrokedShape(shape));
		switch (operation) {
		case ADD:
			area1.add(area2);
			break;
		case SUBTRACT:
			area1.subtract(area2);
			break;
		case INTERSECT:
			area1.intersect(area2);
			break;
		case DIFFERENCE:
			area1.exclusiveOr(area2);
			break;
		}
		return area1;
	}
}
