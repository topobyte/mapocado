/* Osm4j is a library that provides utilities to handle Openstreetmap data in java.
 *
 * Copyright (C) 2011  Sebastian Kuerten
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

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
