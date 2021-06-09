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
