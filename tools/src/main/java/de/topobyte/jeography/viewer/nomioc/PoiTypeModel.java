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

import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.model.SqPoiType;

public class PoiTypeModel extends DefaultComboBoxModel<PoiTypeEntry>
{

	private static final long serialVersionUID = 3799553142149397869L;

	final static Logger logger = LoggerFactory.getLogger(PoiTypeModel.class);

	public PoiTypeModel(IConnection connection)
	{
		addElement(new PoiTypeEntry(Category.STREETS, null));
		addElement(new PoiTypeEntry(Category.POI, null));
		try {
			List<SqPoiType> poiTypes = Dao.getTypes(connection);
			for (SqPoiType type : poiTypes) {
				PoiTypeEntry entry = new PoiTypeEntry(Category.POI, type);
				addElement(entry);
			}
		} catch (QueryException e) {
			logger.error("error while getting poi types", e);
		}
	}

}
