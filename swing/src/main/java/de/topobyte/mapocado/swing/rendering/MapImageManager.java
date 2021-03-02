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
