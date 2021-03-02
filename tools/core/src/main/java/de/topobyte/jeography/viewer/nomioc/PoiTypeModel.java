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
