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

package de.topobyte.mapocado.styles.ui.convert;

import javax.swing.JList;

public class AvailableFilterList extends JList<FilterType>
		implements FilterProvider
{

	private static final long serialVersionUID = -7874385015113662160L;

	private AvailableFilterListModel model;

	public AvailableFilterList()
	{
		model = new AvailableFilterListModel();
		setModel(model);

		FilterSourceTransferHandler sourceTransferHandler = new FilterSourceTransferHandler(
				this);

		setDragEnabled(true);
		setTransferHandler(sourceTransferHandler);
	}

	@Override
	public FilterType getFilter()
	{
		int selectedIndex = getSelectedIndex();
		if (selectedIndex == -1) {
			return null;
		}
		FilterType filter = model.getElementAt(selectedIndex);
		return filter;
	}

}
