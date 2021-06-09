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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.tiles.manager.ImageManager;
import de.topobyte.jeography.tiles.source.ImageSource;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTreeDebugUtil;
import de.topobyte.mapocado.swing.rendering.MapImageManager;
import de.topobyte.mapocado.swing.rendering.MapImageSource;
import de.topobyte.mapocado.swing.theme.Theme;
import de.topobyte.mapocado.swing.theme.ThemeItemListener;
import de.topobyte.mapocado.swing.theme.ThemeListModel;

public class Toolbar extends JPanel implements ActionListener
{

	private static final long serialVersionUID = 3369351350068129478L;

	private List<Viewer> viewers = new ArrayList<>();

	protected JToggleButton buttonNames;
	protected JToggleButton buttonGrid;
	protected JButton buttonReportCache;
	protected JComboBox<Theme> choiceTheme;

	public Toolbar(boolean showThemeSelector, Viewer... viewers)
	{
		for (Viewer viewer : viewers) {
			this.viewers.add(viewer);
		}
		buttonGrid = new JToggleButton("grid",
				this.viewers.get(0).isDrawBorder());
		buttonNames = new JToggleButton("tile names",
				this.viewers.get(0).isDrawTileNumbers());
		buttonReportCache = new JButton("cache report");

		if (showThemeSelector) {
			ThemeListModel themeListModel = new ThemeListModel();
			choiceTheme = new JComboBox<>(themeListModel);
			choiceTheme.setSelectedIndex(0);
		}

		MapocadoViewer viewer = (MapocadoViewer) viewers[0];

		if (showThemeSelector) {
			choiceTheme.addItemListener(new ThemeItemListener(viewer));
		}

		add(buttonGrid);
		add(buttonNames);
		add(buttonReportCache);
		if (showThemeSelector) {
			add(choiceTheme);
		}

		buttonGrid.addActionListener(Toolbar.this);
		buttonNames.addActionListener(Toolbar.this);
		buttonReportCache.addActionListener(Toolbar.this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == buttonGrid) {
			System.out.println("toggle grid");
			for (Viewer viewer : viewers) {
				viewer.setDrawBorder(buttonGrid.isSelected());
			}
		} else if (e.getSource() == buttonNames) {
			System.out.println("toggle names");
			for (Viewer viewer : viewers) {
				viewer.setDrawTileNumbers(buttonNames.isSelected());
			}
		} else if (e.getSource() == buttonReportCache) {
			System.out.println("cache report!");
			for (Viewer viewer : viewers) {
				ImageManager<Tile, BufferedImage> managerBase = viewer
						.getImageManagerBase();
				ImageManager<Tile, BufferedImage> managerOverlay = viewer
						.getImageManagerOverlay();
				report(managerBase);
				report(managerOverlay);
			}
		}
	}

	private void report(ImageManager<Tile, BufferedImage> manager)
	{
		if (manager instanceof MapImageManager) {
			MapImageManager mii = (MapImageManager) manager;
			ImageSource<Tile, BufferedImage> source = mii.getImageSource();
			MapImageSource mis = (MapImageSource) source;
			Mapfile mapfile = mis.getMapfile();

			IntervalTree<Integer, DiskTree<Way>> intervalTree = mapfile
					.getTreeWays();
			List<Integer> starts = intervalTree.getIntervalStarts();
			List<Integer> iter = new ArrayList<>();

			iter.add(0);
			iter.addAll(starts);

			for (int start : iter) {
				DiskTree<Way> tree = intervalTree.getObject(start);
				System.out.println("Tree starting at level: " + start);
				DiskTreeDebugUtil.printCacheDebug(tree);
			}
		}
	}

}