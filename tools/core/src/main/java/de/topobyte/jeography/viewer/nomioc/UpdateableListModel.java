package de.topobyte.jeography.viewer.nomioc;

import javax.swing.ListModel;

import de.topobyte.luqe.iface.QueryException;

public interface UpdateableListModel<E> extends ListModel<E>
{

	public void update(String text) throws QueryException;

}
