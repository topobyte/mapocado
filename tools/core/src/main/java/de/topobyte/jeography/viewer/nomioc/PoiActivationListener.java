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

import de.topobyte.nomioc.luqe.model.SqPoi;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public interface PoiActivationListener
{

	/**
	 * Called when a poi has been activated.
	 * 
	 * @param poi
	 *            the poi activated.
	 */
	public void poiActivated(SqPoi poi);

}
