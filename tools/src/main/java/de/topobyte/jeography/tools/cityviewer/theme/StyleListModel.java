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

package de.topobyte.jeography.tools.cityviewer.theme;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

public class StyleListModel extends DefaultComboBoxModel<Style>
{

	private static final long serialVersionUID = 3892577607792883341L;

	private List<Style> styles = new ArrayList<>();

	public StyleListModel(List<Style> styles)
	{
		this.styles.addAll(styles);
	}

	@Override
	public int getSize()
	{
		return styles.size();
	}

	@Override
	public Style getElementAt(int index)
	{
		return styles.get(index);
	}

}
