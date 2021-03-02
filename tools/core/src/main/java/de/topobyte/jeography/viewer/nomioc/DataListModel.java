package de.topobyte.jeography.viewer.nomioc;

import javax.swing.ListModel;

public interface DataListModel<E> extends ListModel<E>
{

	public Object getObject(int index);

}
