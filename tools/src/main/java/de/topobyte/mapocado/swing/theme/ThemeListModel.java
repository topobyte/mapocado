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

package de.topobyte.mapocado.swing.theme;

import javax.swing.DefaultComboBoxModel;

public class ThemeListModel extends DefaultComboBoxModel<Theme>
{

	private static final long serialVersionUID = 3817558878944634535L;

	private Theme[] themes = new Theme[] {
			new Theme("Default", "config/styles/style-default.zip"),
			new Theme("Inverted RGB", "config/styles/style-inverted-rgb.zip"),
			new Theme("Inverted Luminance",
					"config/styles/style-inverted-luminance.zip"),
			new Theme("Black & White light",
					"config/styles/style-bw-light.zip"),
			new Theme("Black & White dark", "config/styles/style-bw-dark.zip"),
			new Theme("Shifted Pink", "config/styles/style-pink.zip"),
			new Theme("Mapnik", "config/styles/style-mapnik.zip"),
			new Theme("Trains", "config/styles/style-train.zip") };

	@Override
	public int getSize()
	{
		return themes.length;
	}

	@Override
	public Theme getElementAt(int index)
	{
		return themes[index];
	}

}
