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

package de.topobyte.mapocado.android.rendering.gather;

import com.slimjars.dist.gnu.trove.procedure.TIntProcedure;

import de.topobyte.mapocado.android.style.RenderClassHelper;
import de.topobyte.mapocado.android.style.RenderElementGatherer;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.rtree.disk.ElementCallback;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;

public class RelationGatherer implements ElementCallback<Relation>
{

	private RenderElementGatherer renderGatherer;
	private RenderClassHelper renderClassHelper;
	private Filler filler = new Filler();
	private Relation relation;
	private int zoom;

	public RelationGatherer(int zoom, RenderClassHelper renderClassHelper,
			RenderElementGatherer renderGatherer)
	{
		this.renderClassHelper = renderClassHelper;
		this.renderGatherer = renderGatherer;
		this.zoom = zoom;
	}

	@Override
	public void handle(Relation relation)
	{
		this.relation = relation;
		relation.getClasses().forEach(filler);
	}

	private class Filler implements TIntProcedure
	{

		@Override
		public boolean execute(int ref)
		{
			int[] classIds = renderClassHelper.getObjectClassIds(ref);
			for (int objectClassId : classIds) {
				ObjectClass objectClass = renderClassHelper
						.getObjectClass(objectClassId);
				if (zoom < objectClass.minZoom || zoom > objectClass.maxZoom) {
					continue;
				}

				int size = objectClass.elements.length;
				for (int i = 0; i < size; i++) {
					int[] ids = renderClassHelper
							.getRenderElementIds(objectClassId);
					for (int id : ids) {
						renderGatherer.addRelation(id, relation.getPolygon());
					}
				}
			}
			return true;
		}
	};

}
