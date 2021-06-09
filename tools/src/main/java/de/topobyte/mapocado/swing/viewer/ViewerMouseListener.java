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

package de.topobyte.mapocado.swing.viewer;

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import de.topobyte.geomath.WGS84;
import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.tiles.manager.ImageManager;
import de.topobyte.jeography.tiles.manager.ImageManagerSourceRam;
import de.topobyte.jeography.tiles.manager.PriorityImageManager;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.melon.casting.CastUtil;

public class ViewerMouseListener extends MouseAdapter
{

	private final Viewer viewer;
	private final boolean overlay;

	public ViewerMouseListener(Viewer viewer, boolean overlay)
	{
		this.viewer = viewer;
		this.overlay = overlay;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		int onmask = InputEvent.CTRL_DOWN_MASK;
		boolean ctrl = (e.getModifiersEx() & onmask) == onmask;
		if (ctrl) {
			redraw(e);
		}
	}

	private Tile getTile(MouseEvent e)
	{
		double lon = viewer.getMapWindow().getPositionLon(e.getX());
		double lat = viewer.getMapWindow().getPositionLat(e.getY());
		int zoom = viewer.getMapWindow().getZoomLevel();
		double tileLon = WGS84.lon2merc(lon, 1 << zoom);
		double tileLat = WGS84.lat2merc(lat, 1 << zoom);
		// System.out.println(String.format("%.2f, %.2f", tileLon, tileLat));
		Tile tile = new Tile(zoom, (int) tileLon, (int) tileLat);
		return tile;
	}

	private void redraw(MouseEvent e)
	{
		Tile tile = getTile(e);
		ImageManager<Tile, BufferedImage> imageManager = null;
		if (overlay) {
			imageManager = viewer.getImageManagerOverlay();
		} else {
			imageManager = viewer.getImageManagerBase();
		}
		if (imageManager instanceof PriorityImageManager) {
			ImageManagerSourceRam<Tile, BufferedImage> providerManager = CastUtil
					.cast(imageManager);
			providerManager.unchache(tile);
		}
		viewer.repaint();
	}

}
