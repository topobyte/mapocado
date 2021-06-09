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

package de.topobyte.jeography.tools.cityviewer.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.jeography.core.OverlayPoint;
import de.topobyte.jeography.tools.cityviewer.CityViewer;
import de.topobyte.jeography.viewer.action.SimpleAction;
import de.topobyte.jeography.viewer.core.Viewer;
import de.topobyte.jeography.viewer.nomioc.PoiActivationListener;
import de.topobyte.jeography.viewer.nomioc.RoadActivationListener;
import de.topobyte.jeography.viewer.nomioc.SearchUI;
import de.topobyte.mercatorcoordinates.GeoConv;
import de.topobyte.nomioc.luqe.model.SqPoi;
import de.topobyte.nomioc.luqe.model.SqRoad;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class SearchAction extends SimpleAction
		implements RoadActivationListener, PoiActivationListener
{

	private static final long serialVersionUID = -7110246291534835462L;

	static final Logger logger = LoggerFactory.getLogger(SearchAction.class);

	private static String FILE = "res/images/edit-find.png";

	private CityViewer cityViewer;

	/**
	 * Create a new SearchAction.
	 * 
	 * @param cityViewer
	 *            the searcher to use.
	 */
	public SearchAction(CityViewer cityViewer)
	{
		super("search", "search for streets or points of interest");
		setIconFromResource(FILE);
		this.cityViewer = cityViewer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// DbRoot dbRoot = searcher.getDbRoot();
		// SearchUI searchUI = new SearchUI(dbRoot);
		Connection connex = cityViewer.getConnection();

		if (connex == null) {
			logger.debug("database connection not available");
			JOptionPane.showInternalMessageDialog(cityViewer.getContentPane(),
					"No database connection available. ");
			return;
		}

		SearchUI searchUI;
		try {
			searchUI = new SearchUI(connex, false);
			JDialog diag = new JDialog(cityViewer, "Search...");
			diag.setContentPane(searchUI);

			diag.setSize(new Dimension(500, 400));
			diag.setVisible(true);

			searchUI.addRoadActivationListener(this);
			searchUI.addPoiActivationListener(this);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void roadActivated(SqRoad road)
	{
		System.out.println("road activated...");
		Viewer viewer = cityViewer.getViewer();
		// DbCoordinate mean = road.getMean();

		double lon = GeoConv.mercatorToLongitude(road.getX());
		double lat = GeoConv.mercatorToLatitude(road.getY());
		Set<OverlayPoint> points = new HashSet<>();
		// points.add(new OverlayPoint(mean.getLon(), mean.getLat()));
		points.add(new OverlayPoint(lon, lat));
		viewer.setOverlayPoints(points);
		viewer.gotoOverlayPoints();
		viewer.repaint();
	}

	@Override
	public void poiActivated(SqPoi poi)
	{
		System.out.println("poi activated...");
		Viewer viewer = cityViewer.getViewer();
		// DbCoordinate mean = road.getMean();
		double lon = GeoConv.mercatorToLongitude(poi.getX());
		double lat = GeoConv.mercatorToLatitude(poi.getY());
		Set<OverlayPoint> points = new HashSet<>();
		// points.add(new OverlayPoint(mean.getLon(), mean.getLat()));
		points.add(new OverlayPoint(lon, lat));
		viewer.setOverlayPoints(points);
		viewer.gotoOverlayPoints();
		viewer.repaint();
	}

}
