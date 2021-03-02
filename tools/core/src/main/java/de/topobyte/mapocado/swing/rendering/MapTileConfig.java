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
