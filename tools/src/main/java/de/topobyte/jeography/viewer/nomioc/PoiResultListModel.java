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

package de.topobyte.jeography.viewer.nomioc;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;

import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.dao.MatchMode;
import de.topobyte.nomioc.luqe.dao.SortOrder;
import de.topobyte.nomioc.luqe.model.SqPoi;
import de.topobyte.swing.util.DefaultElementWrapper;
import de.topobyte.swing.util.ElementWrapper;

public class PoiResultListModel
		extends AbstractResultListModel<ElementWrapper<SqPoi>>
		implements UpdateableDataListModel<ElementWrapper<SqPoi>>
{

	private List<ElementWrapper<SqPoi>> results;
	private static final int max = 20;
	private IConnection connection;
	private PoiTypeEntry poiType;
	private boolean fillTypes;

	public PoiResultListModel(IConnection connection, PoiTypeEntry poiType,
			boolean fillTypes)
	{
		this.connection = connection;
		this.poiType = poiType;
		this.fillTypes = fillTypes;
	}

	@Override
	public void update(String textNew) throws QueryException
	{
		List<SqPoi> list;
		if (poiType.getType() == null) {
			list = Dao.getPois(connection, textNew, MatchMode.ANYWHERE,
					SortOrder.ASCENDING, max, 0);
		} else {
			int poiTypeId = Dao.getPoiTypeId(connection,
					poiType.getType().getName());
			TIntSet types = new TIntHashSet();
			types.add(poiTypeId);
			list = Dao.getPois(connection, textNew, MatchMode.ANYWHERE,
					SortOrder.ASCENDING, types, max, 0);
		}
		if (fillTypes) {
			list = Dao.fillTypes(connection, list);
		}

		List<ElementWrapper<SqPoi>> newResults = new ArrayList<>();
		for (SqPoi poi : list) {
			newResults.add(new ElementWrapperImpl(poi));
		}
		results = newResults;

		fire(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
	}

	@Override
	public ElementWrapper<SqPoi> getElementAt(int index)
	{
		if (results.size() <= index) {
			return null;
		}
		return results.get(index);
	}

	@Override
	public int getSize()
	{
		if (results == null) {
			return 0;
		}
		return results.size() > max ? max : results.size();
	}

	@Override
	public SqPoi getObject(int index)
	{
		return results.get(index).getElement();
	}

	private String toString(SqPoi element)
	{
		try {
			return element.toVerboseString(connection, false);
		} catch (QueryException e) {
			e.printStackTrace();
		}
		return element.toString();
	}

	private class ElementWrapperImpl extends DefaultElementWrapper<SqPoi>
	{

		public ElementWrapperImpl(SqPoi element)
		{
			super(element);
		}

		@Override
		public String toString()
		{
			return PoiResultListModel.this.toString(element);
		}

	}

}
