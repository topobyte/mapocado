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

package de.topobyte.mapocado.styles.classes.element;

import java.util.HashSet;
import java.util.Set;

import de.topobyte.mapocado.styles.rules.enums.Simplification;

public class ObjectClassRef
{

	private final String ref;
	private final Simplification simplification;

	private Set<String> mandatoryKeepKeys = new HashSet<>();
	private Set<String> optionalKeepKeys = new HashSet<>();

	private int minZoom;
	private int maxZoom;

	public ObjectClassRef(String ref, Simplification simplification)
	{
		this.ref = ref;
		this.simplification = simplification;
	}

	public String getRef()
	{
		return ref;
	}

	public Set<String> getMandatoryKeepKeys()
	{
		return mandatoryKeepKeys;
	}

	public Set<String> getOptionalKeepKeys()
	{
		return optionalKeepKeys;
	}

	public Simplification getSimplification()
	{
		return simplification;
	}

	public void setMinZoom(int minZoom)
	{
		this.minZoom = minZoom;
	}

	public void setMaxZoom(int maxZoom)
	{
		this.maxZoom = maxZoom;
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
		return "ObjectClassRef ref: '" + ref + "', simplify: "
				+ simplification.toString();
	}

}
