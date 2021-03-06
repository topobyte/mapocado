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
import de.topobyte.jeography.tiles.manager.ImageManager;
import de.topobyte.jeography.viewer.config.TileConfig;
import de.topobyte.jeography.viewer.core.PaintListener;
import de.topobyte.mapocado.mapformat.Mapfile;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapTileConfig implements TileConfig
{

	private final int id;
	private final String name;
	private final Mapfile mapfile;
	private final MapRenderConfig renderConfig;
	private final int tileSize;

	public MapTileConfig(int id, String name, Mapfile mapfile,
			MapRenderConfig renderConfig, int tileSize)
	{
		this.id = id;
		this.name = name;
		this.mapfile = mapfile;
		this.renderConfig = renderConfig;
		this.tileSize = tileSize;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public ImageManager<Tile, BufferedImage> createImageManager()
	{
		return new MapImageManager(mapfile, renderConfig, tileSize);
	}

	public Mapfile getMapfile()
	{
		return mapfile;
	}

	@Override
	public PaintListener createGlobalManager()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
