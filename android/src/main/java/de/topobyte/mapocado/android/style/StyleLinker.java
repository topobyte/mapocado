// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.mapocado.android.style;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.styles.classes.element.Caption;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.PathText;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.slim.CaptionSlim;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;

public class StyleLinker
{

	/**
	 * Dynamically link the tags of RenderElements reachable from the specified
	 * List of ObjectClasses to the ids used for those tags within the specified
	 * StringPool. RenderElements that rely on a value stored for a key, but
	 * whose key is not available within the StringPool (because it has not been
	 * incorporated into the mapfile) will be discarded.
	 * 
	 * @param objectClasses
	 *            the object classes to operate on.
	 * @param poolForKeepKeys
	 *            the StringPool to use for linking.
	 */
	public static void createSlimClasses(List<ObjectClass> objectClasses,
			StringPool poolForKeepKeys)
	{
		for (ObjectClass objectClass : objectClasses) {
			RenderElement[] elements = objectClass.elements;
			ArrayList<RenderElement> slimElements = new ArrayList<>(
					elements.length);
			for (int i = 0; i < elements.length; i++) {
				RenderElement element = elements[i];
				if (element instanceof PathText) {
					PathText p = (PathText) element;
					String key = p.getKey();
					if (!poolForKeepKeys.containsString(key)) {
						continue;
					}
					int keyId = poolForKeepKeys.getId(key);
					PathTextSlim slim = new PathTextSlim(p.getLevel(), keyId,
							p.getFontSize(), p.getStrokeWidth(), p.getFill(),
							p.getStroke(), p.getFontFamily(), p.getFontStyle());
					slimElements.add(slim);
				} else if (element instanceof Caption) {
					Caption c = (Caption) element;
					String key = c.getKey();
					if (!poolForKeepKeys.containsString(key)) {
						continue;
					}
					int keyId = poolForKeepKeys.getId(key);
					CaptionSlim slim = new CaptionSlim(c.getLevel(), c.getTag(),
							keyId, c.hasDeltaY(), c.getDy(), c.getFontSize(),
							c.getStrokeWidth(), c.getFill(), c.getStroke(),
							c.getFontFamily(), c.getFontStyle());
					slimElements.add(slim);
				} else {
					slimElements.add(element);
				}
			}
			objectClass.elements = slimElements.toArray(new RenderElement[0]);
		}
	}

}
