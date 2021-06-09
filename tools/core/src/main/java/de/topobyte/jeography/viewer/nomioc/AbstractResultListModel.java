package de.topobyte.jeography.viewer.nomioc;

import java.util.HashSet;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public abstract class AbstractResultListModel<E> implements ListModel<E>
{

	private Set<ListDataListener> listeners = new HashSet<>();

	@Override
	public void addListDataListener(ListDataListener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l)
	{
		listeners.remove(l);
	}

	/**
	 * Propagate the given event to all registered ListDataListeners.
	 * 
	 * @param e
	 *            the event to propagate.
	 */
	public void fire(ListDataEvent e)
	{
		for (ListDataListener l : listeners) {
			l.contentsChanged(e);
		}
	}
}
