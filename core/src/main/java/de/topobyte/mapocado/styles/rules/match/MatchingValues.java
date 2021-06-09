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

package de.topobyte.mapocado.styles.rules.match;

import java.util.Set;
import java.util.TreeSet;

public class MatchingValues
{

	private boolean hasWildcard = false;
	private Set<String> values = new TreeSet<>();

	public void add(String value)
	{
		values.add(value);
	}

	public Set<String> getValues()
	{
		return values;
	}

	public void setHasWildcard(boolean has)
	{
		hasWildcard = has;
	}

	public boolean hasWildcard()
	{
		return hasWildcard;
	}

}
