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

package de.topobyte.mapocado.styles.convert;

import java.awt.Color;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.color.util.CieLabUtil;
import de.topobyte.mapocado.swing.rendering.Conversion;

public class ConverterStoreLuminanceLab implements ColorConverter
{

	private String storageKey;

	public ConverterStoreLuminanceLab(String storageKey)
	{
		this.storageKey = storageKey;
	}

	public String getStorageKey()
	{
		return storageKey;
	}

	public void setStorageKey(String storageKey)
	{
		this.storageKey = storageKey;
	}

	@Override
	public ColorCode convert(ConversionContext context, ColorCode color)
	{
		Color c = Conversion.getColor(color);

		float ciel = CieLabUtil.getLuminance(c);
		context.store(storageKey, ciel);

		return color;
	}
}
