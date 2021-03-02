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

package de.topobyte.jeography.tools.cityviewer.action;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.jeography.viewer.action.SimpleAction;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class QuitAction extends SimpleAction
{

	private static final long serialVersionUID = -7110246291534835462L;

	static final Logger logger = LoggerFactory.getLogger(QuitAction.class);

	private static String FILE = "res/images/gtk-quit.png";

	/**
	 * Create a new SearchAction.
	 */
	public QuitAction()
	{
		super("quit", "exit the program");
		setIconFromResource(FILE);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

}
