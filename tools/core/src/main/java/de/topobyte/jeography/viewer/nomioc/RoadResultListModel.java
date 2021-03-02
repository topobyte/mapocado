package de.topobyte.jeography.viewer.nomioc;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.dao.MatchMode;
import de.topobyte.nomioc.luqe.dao.SortOrder;
import de.topobyte.nomioc.luqe.model.SqRoad;
import de.topobyte.swing.util.DefaultElementWrapper;
import de.topobyte.swing.util.ElementWrapper;

public class RoadResultListModel
		extends AbstractResultListModel<ElementWrapper<SqRoad>>
		implements UpdateableDataListModel<ElementWrapper<SqRoad>>
{

	private List<ElementWrapper<SqRoad>> results;
	private static final int max = 20;
	private IConnection connection;

	public RoadResultListModel(IConnection connection)
	{
		this.connection = connection;
	}

	@Override
	public void update(String textNew) throws QueryException
	{
		List<SqRoad> list = Dao.getRoads(connection, textNew,
				MatchMode.ANYWHERE, SortOrder.ASCENDING, max, 0);

		List<ElementWrapper<SqRoad>> newResults = new ArrayList<>();
		for (SqRoad poi : list) {
			newResults.add(new ElementWrapperImpl(poi));
		}
		results = newResults;

		fire(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0));
	}

	@Override
	public ElementWrapper<SqRoad> getElementAt(int index)
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
	public SqRoad getObject(int index)
	{
		return results.get(index).getElement();
	}

	public String toString(SqRoad element)
	{
		try {
			return element.toVerboseString(connection, false);
		} catch (QueryException e) {
			e.printStackTrace();
		}
		return element.toString();
	}

	private class ElementWrapperImpl extends DefaultElementWrapper<SqRoad>
	{

		public ElementWrapperImpl(SqRoad element)
		{
			super(element);
		}

		@Override
		public String toString()
		{
			return RoadResultListModel.this.toString(element);
		}

	}

}
