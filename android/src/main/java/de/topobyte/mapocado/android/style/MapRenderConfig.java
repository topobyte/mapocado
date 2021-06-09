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

package de.topobyte.mapocado.android.style;

import java.io.File;
import java.util.List;

import de.topobyte.mapocado.android.rendering.StyleConversion;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.directory.StyleDirectory;

public class MapRenderConfig
{

	private int backgroundColor;
	private int mapOverlayInner;
	private int mapOverlayOuter;
	private int mapOverlayGpsInner;
	private int mapOverlayGpsOuter;
	private StyleDirectory style;

	public MapRenderConfig(StyleDirectory style)
	{
		this.backgroundColor = StyleConversion
				.getColor(style.getBackgroundColor());
		this.mapOverlayInner = StyleConversion
				.getColor(style.getMapOverlayInner());
		this.mapOverlayOuter = StyleConversion
				.getColor(style.getMapOverlayOuter());
		this.mapOverlayGpsInner = StyleConversion
				.getColor(style.getMapOverlayGpsInner());
		this.mapOverlayGpsOuter = StyleConversion
				.getColor(style.getMapOverlayGpsOuter());
		this.style = style;
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public int getOverlayInner()
	{
		return mapOverlayInner;
	}

	public int getOverlayOuter()
	{
		return mapOverlayOuter;
	}

	public int getOverlayGpsInner()
	{
		return mapOverlayGpsInner;
	}

	public int getOverlayGpsOuter()
	{
		return mapOverlayGpsOuter;
	}

	public List<ObjectClass> getObjectClasses()
	{
		return style.getObjectClasses();
	}

	public File getTexture(String patternName)
	{
		return style.getPattern(patternName);
	}

	public File getSymbol(String source)
	{
		return style.getSymbol(source);
	}

	public StyleDirectory getStyleDirectory()
	{
		return style;
	}

}