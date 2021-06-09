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

package de.topobyte.jeography.viewer.nomioc;

import de.topobyte.nomioc.luqe.model.SqPoiType;

public class PoiTypeEntry
{

	private final Category category;
	private final SqPoiType type;

	public PoiTypeEntry(Category category, SqPoiType type)
	{
		this.category = category;
		this.type = type;
	}

	@Override
	public String toString()
	{
		if (category == Category.STREETS) {
			return "Streets";
		}
		if (type == null) {
			return "All pois";
		}
		return type.getName();
	}

	public Category getCategory()
	{
		return category;
	}

	public SqPoiType getType()
	{
		return type;
	}

}
