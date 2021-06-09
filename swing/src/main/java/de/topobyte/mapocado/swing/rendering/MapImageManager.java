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

package de.topobyte.mapocado.swing.rendering;

import java.awt.image.BufferedImage;

import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.tiles.manager.ImageManagerSourceRam;
import de.topobyte.mapocado.mapformat.Mapfile;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapImageManager extends ImageManagerSourceRam<Tile, BufferedImage>
{

	public MapImageManager(Mapfile mapfile, MapRenderConfig renderConfig,
			int tileSize)
	{
		super(1, 48, new MapImageSource(mapfile, renderConfig, tileSize));
	}

	public void setTileSize(int tileSize)
	{
		((MapImageSource) getImageSource()).setTileSize(tileSize);
	}

}
