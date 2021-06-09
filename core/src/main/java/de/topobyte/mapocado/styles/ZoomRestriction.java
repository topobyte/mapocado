// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

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
