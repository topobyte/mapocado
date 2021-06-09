package de.topobyte.jeography.viewer.nomioc;

import de.topobyte.nomioc.luqe.model.SqRoad;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public interface RoadActivationListener
{

	/**
	 * Called when a road has been activated.
	 * 
	 * @param road
	 *            the road activated.
	 */
	public void roadActivated(SqRoad road);

}
