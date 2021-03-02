package de.topobyte.mapocado.swing.rendering;

import java.awt.image.BufferedImage;

import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.tiles.manager.ImageManager;
import de.topobyte.jeography.tiles.manager.ProviderImageManager;
import de.topobyte.jeography.viewer.config.TileConfig;
import de.topobyte.jeography.viewer.core.PaintListener;
import de.topobyte.mapocado.mapformat.Mapfile;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class TreeLeafTileConfig implements TileConfig
{

	private final int id;
	private final String name;
	private final Mapfile mapDataProvider;
	private int tileSize;

	public TreeLeafTileConfig(int id, String name, Mapfile mapDataProvider,
			int tileSize)
	{
		this.id = id;
		this.name = name;
		this.mapDataProvider = mapDataProvider;
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
		return new ProviderImageManager<>(
				new TreeLeafImageProvider(mapDataProvider, 1, tileSize));
	}

	@Override
	public PaintListener createGlobalManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
