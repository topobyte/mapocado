package de.topobyte.jeography.viewer.nomioc;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

import com.google.common.base.Joiner;
import com.slimjars.dist.gnu.trove.map.TIntObjectMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;
import com.slimjars.dist.gnu.trove.set.TIntSet;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.luqe.jdbc.JdbcConnection;
import de.topobyte.mercatorcoordinates.GeoConv;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.model.SqPoi;
import de.topobyte.nomioc.luqe.model.SqPoiType;
import de.topobyte.nomioc.luqe.model.SqRoad;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class TestSearchUI
{

	public static void main(String args[])
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, QueryException
	{
		Class.forName("org.sqlite.JDBC").newInstance();
		String url = "jdbc:sqlite:" + "/tmp/Berlin.sqlite";
		Connection connection = DriverManager.getConnection(url, "username",
				"password");

		JFrame frame = new JFrame();
		frame.setSize(new Dimension(500, 400));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final TIntObjectMap<String> typeNames = new TIntObjectHashMap<>();
		IConnection connex = new JdbcConnection(connection);
		List<SqPoiType> types = Dao.getTypes(connex);
		for (SqPoiType type : types) {
			typeNames.put(type.getId(), type.getName());
		}

		SearchUI searchUI = new SearchUI(connection, true);
		frame.setContentPane(searchUI);

		frame.setVisible(true);

		searchUI.addRoadActivationListener(new RoadActivationListener() {

			@Override
			public void roadActivated(SqRoad road)
			{
				double lon = GeoConv.mercatorToLongitude(road.getX());
				double lat = GeoConv.mercatorToLatitude(road.getY());
				System.out.println(String.format("activated: %s, %f %f",
						road.getNameSafe(), lon, lat));
			}
		});

		searchUI.addPoiActivationListener(new PoiActivationListener() {

			@Override
			public void poiActivated(SqPoi poi)
			{
				double lon = GeoConv.mercatorToLongitude(poi.getX());
				double lat = GeoConv.mercatorToLatitude(poi.getY());
				System.out.println(String.format("activated: %s, %f %f",
						poi.getNameSafe(), lon, lat));
				TIntSet types = poi.getTypes();
				int[] ids = types.toArray();
				Arrays.sort(ids);
				List<String> names = new ArrayList<>();
				for (int id : ids) {
					names.add(typeNames.get(id));
				}
				System.out.println("types: " + Joiner.on(", ").join(names));
			}
		});
	}

}