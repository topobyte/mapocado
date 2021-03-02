package de.topobyte.mapocado.styles;

import de.topobyte.mapocado.styles.rules.Rule;

public class ZoomRestriction
{

	private int minZoom = 0;
	private int maxZoom = 0;

	public ZoomRestriction(Rule rule)
	{
		minZoom = rule.getZoomMin();
		maxZoom = rule.getZoomMax();
	}

	public ZoomRestriction(int minZoom, int maxZoom)
	{
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	public ZoomRestriction getNewRestriction(Rule rule)
	{
		ZoomRestriction restriction = new ZoomRestriction(minZoom, maxZoom);
		restriction.applyRule(rule);
		return restriction;
	}

	private void applyRule(Rule rule)
	{
		if (rule.getZoomMin() > minZoom) {
			minZoom = rule.getZoomMin();
		}
		if (rule.getZoomMax() < maxZoom) {
			maxZoom = rule.getZoomMax();
		}
	}

	public int getMinZoom()
	{
		return minZoom;
	}

	public int getMaxZoom()
	{
		return maxZoom;
	}

	@Override
	public String toString()
	{
		return "min: " + minZoom + ", max: " + maxZoom;
	}
}
