package de.topobyte.mapocado.mapformat;

import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.util.ObjectClassLookup;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;

public class SegmentationHelper
{

	public static int getMinimumZoomLevel(Entity element,
			ObjectClassLookup classLookup)
	{
		int minZoom = Integer.MAX_VALUE;
		for (int refId : element.getClasses().toArray()) {
			ObjectClassRef ref = classLookup.get(refId);
			int min = ref.getMinZoom();
			if (min < minZoom) {
				minZoom = min;
			}
		}
		return minZoom;
	}

}
