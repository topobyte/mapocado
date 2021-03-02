package de.topobyte.mapocado.rendering.text;

import java.util.List;

import com.infomatiq.jsi.Rectangle;

import de.topobyte.jsi.GenericRTree;
import de.topobyte.mapocado.rendering.geom.GeneralRectangle;

/**
 * An implementation of the {@link TextIntersectionChecker} interface based on
 * an rtree for efficient retrieval of objects and JTS for primitive
 * intersection testing.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class TextIntersectionCheckerTree implements TextIntersectionChecker
{

	private GenericRTree<int[]> regions = new GenericRTree<>(2, 8);

	@Override
	public void add(int[][] boxes)
	{
		// add each box to the tree
		for (int[] box : boxes) {
			Rectangle rect = GeneralRectangle.getBoundingBox(box);
			regions.add(rect, box);
		}
	}

	@Override
	public boolean isValid(int[][] boxes)
	{
		// check each box separately
		for (int[] box : boxes) {
			Rectangle rect = GeneralRectangle.getBoundingBox(box);
			List<int[]> candidates = regions.intersectionsAsList(rect);
			// test against each candidate
			for (int[] region : candidates) {
				try {
					if (GeneralRectangle.intersects(box, region)) {
						return false;
					}
				} catch (Exception e) {
					return false;
				}
			}
		}
		return true;
	}

}