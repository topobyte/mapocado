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

package de.topobyte.mapocado.swing.rendering.labels;

public class RenderClass
{

	// Identifier used by the LabelDrawer internally
	int classId;
	// Identifier for database queries to the sqlite database, -1 for mapfile
	// classes
	int typeId;

	int minZoom;
	int maxZoom;
	LabelClass labelClass;

	public RenderClass(int classId, int typeId, int minZoom, int maxZoom,
			LabelClass labelClass)
	{
		this.classId = classId;
		this.typeId = typeId;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.labelClass = labelClass;
	}

}
