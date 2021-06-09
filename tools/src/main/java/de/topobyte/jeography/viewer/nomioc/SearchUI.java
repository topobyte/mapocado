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

package de.topobyte.jeography.viewer.nomioc;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.luqe.jdbc.JdbcConnection;
import de.topobyte.nomioc.luqe.model.SqPoi;
import de.topobyte.nomioc.luqe.model.SqRoad;
import de.topobyte.swing.util.DocumentAdapter;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class SearchUI extends JPanel
		implements ActionListener, ListSelectionListener
{

	private static final long serialVersionUID = 1623220323023117627L;

	final static Logger logger = LoggerFactory.getLogger(SearchUI.class);

	ActivatableJList listResults;
	UpdateableDataListModel resultModel;

	private final IConnection connex;
	private boolean fillTypes;
	private PoiTypeEntry activeEntry;

	/**
	 * Constructor.
	 * 
	 * @param connection
	 *            a connection to use for underlying queries.
	 * @throws SQLException
	 * 
	 */
	public SearchUI(Connection connection, boolean fillTypes)
			throws SQLException
	{
		this.fillTypes = fillTypes;
		this.connex = new JdbcConnection(connection);

		PoiTypeModel poiTypeModel = new PoiTypeModel(connex);

		final JComboBox<PoiTypeEntry> combo = new JComboBox<>(poiTypeModel);
		final JTextField inRoad = new JTextField("");

		resultModel = new RoadResultListModel(connex);

		JScrollPane jspRoad = new JScrollPane();
		listResults = new ActivatableJList(resultModel);
		jspRoad.setViewportView(listResults);

		combo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e)
			{
				if (e.getStateChange() == ItemEvent.SELECTED) {
					try {
						setPoiType((PoiTypeEntry) e.getItem());
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		Object activeItem = combo.getSelectedItem();
		PoiTypeEntry activeEntry = (PoiTypeEntry) activeItem;
		setPoiType(activeEntry);

		listResults.addActionListener(this);

		inRoad.getDocument().addDocumentListener(new DocumentAdapter() {

			@Override
			public void update(DocumentEvent e)
			{
				try {
					resultModel.update(inRoad.getText());
				} catch (QueryException e1) {
					e1.printStackTrace();
				}
			}
		});

		listResults.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Layout

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 1.0;
		c.weighty = 0.0;
		add(combo, c);
		add(inRoad, c);
		c.weighty = 1.0;
		add(jspRoad, c);
	}

	private void setPoiType(PoiTypeEntry activeEntry) throws SQLException
	{
		this.activeEntry = activeEntry;
		System.out.println("type: " + activeEntry);

		if (activeEntry.getCategory() == Category.STREETS) {
			resultModel = new RoadResultListModel(connex);
		} else if (activeEntry.getCategory() == Category.POI) {
			resultModel = new PoiResultListModel(connex, activeEntry,
					fillTypes);
		}
		listResults.setModel(resultModel);
	}

	@Override
	// actionListener implementation for both ActivatableJLists.
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == listResults) {
			int sel = listResults.getSelectedIndex();
			Object object = resultModel.getObject(sel);

			if (activeEntry.getCategory() == Category.STREETS) {
				SqRoad road = (SqRoad) object;
				fireRoadActivation(road);
			} else if (activeEntry.getCategory() == Category.POI) {
				SqPoi poi = (SqPoi) object;
				firePoiActivation(poi);
			}
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getValueIsAdjusting()) {
			return;
			// may perform something... TODO: use or remove...
		}
	}

	private Set<RoadActivationListener> roadListeners = new HashSet<>();
	private Set<PoiActivationListener> poiListeners = new HashSet<>();

	/**
	 * Add a listener to be informed on activation of a road.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addRoadActivationListener(RoadActivationListener listener)
	{
		roadListeners.add(listener);
	}

	/**
	 * Remove a listener from the set of listeners.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeRoadActivationListener(RoadActivationListener listener)
	{
		roadListeners.remove(listener);
	}

	/**
	 * Add a listener to be informed on activation of a road.
	 * 
	 * @param listener
	 *            the listener to add.
	 */
	public void addPoiActivationListener(PoiActivationListener listener)
	{
		poiListeners.add(listener);
	}

	/**
	 * Remove a listener from the set of listeners.
	 * 
	 * @param listener
	 *            the listener to remove.
	 */
	public void removePoiActivationListener(PoiActivationListener listener)
	{
		poiListeners.remove(listener);
	}

	private void fireRoadActivation(SqRoad road)
	{
		for (RoadActivationListener listener : roadListeners) {
			listener.roadActivated(road);
		}
	}

	private void firePoiActivation(SqPoi poi)
	{
		for (PoiActivationListener listener : poiListeners) {
			listener.poiActivated(poi);
		}
	}

}