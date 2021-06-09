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

import de.topobyte.mapocado.android.rendering.gather.NodeData;
import de.topobyte.mapocado.android.rendering.gather.PathTextElement;
import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.melon.casting.CastUtil;

public class RenderElementGatherer
{

	public List<Coordinate>[] bucketsCoordinates;
	public List<NodeData>[] bucketsNodes;
	public List<Linestring>[] bucketsWays;
	public List<PathTextElement>[] bucketsPathTexts;
	public List<Multipolygon>[] bucketsRelations;

	public RenderElementGatherer(StringPool poolForRefs,
			RenderClassHelper renderClassHelper)
	{

		int numClasses = poolForRefs.getNumberOfEntries();
		int numRenderElements = 0;
		for (int i = 0; i < numClasses; i++) {
			int[] classIds = renderClassHelper.getObjectClassIds(i);
			for (int objectClassId : classIds) {
				ObjectClass objectClass = renderClassHelper
						.getObjectClass(objectClassId);
				RenderElement[] elements = objectClass.elements;
				numRenderElements += elements.length;
			}
		}

		bucketsCoordinates = CastUtil.cast(new List[numRenderElements]);
		bucketsNodes = CastUtil.cast(new List[numRenderElements]);
		bucketsWays = CastUtil.cast(new List[numRenderElements]);
		bucketsPathTexts = CastUtil.cast(new List[numRenderElements]);
		bucketsRelations = CastUtil.cast(new List[numRenderElements]);
		for (int i = 0; i < bucketsWays.length; i++) {
			bucketsCoordinates[i] = new ArrayList<>();
			bucketsNodes[i] = new ArrayList<>();
			bucketsWays[i] = new ArrayList<>();
			bucketsPathTexts[i] = new ArrayList<>();
			bucketsRelations[i] = new ArrayList<>();
		}
	}

	public void clear()
	{
		for (int i = 0; i < bucketsWays.length; i++) {
			bucketsCoordinates[i].clear();
			bucketsNodes[i].clear();
			bucketsWays[i].clear();
			bucketsPathTexts[i].clear();
			bucketsRelations[i].clear();
		}
	}

	public void addCoordinate(int renderElementId, Coordinate coordinate)
	{
		bucketsCoordinates[renderElementId].add(coordinate);
	}

	public void addNode(int renderElementId, NodeData node)
	{
		bucketsNodes[renderElementId].add(node);
	}

	public void addWay(int renderElementId, Linestring string)
	{
		bucketsWays[renderElementId].add(string);
	}

	public void addPathText(int renderElementId,
			PathTextElement pathTextElement)
	{
		bucketsPathTexts[renderElementId].add(pathTextElement);
	}

	public void addRelation(int renderElementId, Multipolygon polygon)
	{
		bucketsRelations[renderElementId].add(polygon);
	}

}
