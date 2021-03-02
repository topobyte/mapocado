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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class ActivatableJList<E> extends JList<E>
{

	private static final long serialVersionUID = -1582608378498516087L;

	private Set<ActionListener> listeners = new HashSet<>();

	/**
	 * Add the given listener to the set of listeners.
	 * 
	 * @param listener
	 *            a listener to add.
	 */
	public void addActionListener(ActionListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Remove the given listener from the set of listeners.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeActionListener(ActionListener listener)
	{
		listeners.remove(listener);
	}

	private void init()
	{
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1
						&& e.getClickCount() == 2) {
					fire();
				}
			}
		});

		addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					fire();
				}
			}
		});
	}

	void fire()
	{
		for (ActionListener listener : listeners) {
			ActionEvent e = new ActionEvent(this, 1, "activate");
			listener.actionPerformed(e);
		}
	}

	/**
	 * Constructor
	 */
	public ActivatableJList()
	{
		super();
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param listData
	 *            parameter for JList.
	 */
	public ActivatableJList(E[] listData)
	{
		super(listData);
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param listData
	 *            parameter for JList.
	 */
	public ActivatableJList(Vector<E> listData)
	{
		super(listData);
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param model
	 *            parameter for JList.
	 */
	public ActivatableJList(ListModel<E> model)
	{
		super(model);
		init();
	}
}
