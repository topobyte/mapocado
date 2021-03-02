package de.topobyte.mapocado.mapformat.rtree.str;

import java.util.Comparator;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;

public class STRConstructionYComparator<T>
		implements Comparator<STRConstructionElement<T>>
{

	@Override
	public int compare(STRConstructionElement<T> o1,
			STRConstructionElement<T> o2)
	{
		BoundingBox box1 = o1.getBoundingBox();
		BoundingBox box2 = o2.getBoundingBox();

		float y1 = box1.getCenterY();
		float y2 = box2.getCenterY();
		if (y1 < y2) {
			return -1;
		}
		if (y1 > y2) {
			return 1;
		}

		float x1 = box1.getCenterX();
		float x2 = box2.getCenterX();
		if (x1 < x2) {
			return -1;
		}
		if (x1 > x2) {
			return 1;
		}

		return 0;
	}

}
