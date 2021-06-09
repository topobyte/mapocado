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

package de.topobyte.mapocado.swing.rendering.labels;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slimjars.dist.gnu.trove.list.TIntList;
import com.slimjars.dist.gnu.trove.list.array.TIntArrayList;
import com.slimjars.dist.gnu.trove.map.TIntIntMap;
import com.slimjars.dist.gnu.trove.map.TObjectIntMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntIntHashMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;
import com.slimjars.dist.gnu.trove.map.hash.TObjectIntHashMap;
import com.slimjars.dist.gnu.trove.set.TIntSet;
import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.jdbc.JdbcConnection;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.Rule;
import de.topobyte.mapocado.swing.rendering.MapRenderConfig;
import de.topobyte.mapocado.swing.rendering.poi.Categories;
import de.topobyte.mapocado.swing.rendering.poi.DrawingOrder;
import de.topobyte.mapocado.swing.rendering.poi.Group;
import de.topobyte.mapocado.swing.rendering.poi.PoiTypeInfo;
import de.topobyte.mapocado.swing.rendering.poi.category.Category;
import de.topobyte.mapocado.swing.rendering.poi.category.DatabaseCategory;
import de.topobyte.mapocado.swing.rendering.poi.category.MapfileCategory;
import de.topobyte.nomioc.luqe.model.SqPoiType;

public class RenderConfig
{

	final static Logger logger = LoggerFactory.getLogger(RenderConfig.class);

	private final static float DEFAULT_DENSITY = 1;

	private final MapRenderConfig mapRenderConfig;

	private final PoiTypeInfo typesInfo;

	private final TObjectIntMap<String> typeToClassId;
	private final TIntIntMap typeIdToClassId;

	// This defines the rendering order
	private final List<RenderClass> renderClasses;
	private final TIntObjectHashMap<RenderClass> renderClassMap;

	private final TIntSet enabledIds = new TIntHashSet();

	private int idFactory = 0;
	private final Map<String, RenderClass> typeToClass = new HashMap<>();
	private final Map<RenderClass, String> classToType = new HashMap<>();

	public RenderConfig(MapRenderConfig mapRenderConfig, Connection connection)
			throws SQLException
	{
		this.mapRenderConfig = mapRenderConfig;

		IConnection db = new JdbcConnection(connection);

		typesInfo = PoiTypeInfo.getInstance(db);
		typeToClassId = new TObjectIntHashMap<>(10, 0.5f, -1);
		typeIdToClassId = new TIntIntHashMap();

		// Keep a list of types that have not been covered by rules so that we
		// can later add them to the wildcard rule
		Set<String> left = new HashSet<>();
		for (SqPoiType type : typesInfo.getTypes()) {
			left.add(type.getName());
		}

		Rule othersRule = null; // Rule with the wildcard '*'

		for (Rule rule : mapRenderConfig.getLabelRules()) {
			String type = rule.getKey();
			int typeId = typesInfo.getTypeId(type);
			if (typeId >= 0) { // valid database rule
				left.remove(type);
			} else { // invalid database rule,
				// so it's either a mapfile rule or the wildard
				logger.warn("typeId < 0: " + rule.getKey());
				if (rule.getKey().equals("*")) {
					othersRule = rule;
					continue;
				}
			}

			LabelContainer lc = mapRenderConfig.getLabelStyles().get(rule);
			addRule(rule, rule.getKey(), typeId, lc, DEFAULT_DENSITY);
		}

		if (othersRule != null) {
			LabelContainer lc = mapRenderConfig.getLabelStyles()
					.get(othersRule);
			for (String type : left) {
				int typeId = typesInfo.getTypeId(type);
				if (typeId < 0) {
					logger.warn("typeId < 0: " + type);
					continue;
				}
				logger.info("Adding to others: '" + type + "'");
				addRule(othersRule, type, typeId, lc, DEFAULT_DENSITY);
			}
		}

		renderClasses = new ArrayList<>();
		for (String type : DrawingOrder.order) {
			RenderClass renderClass = typeToClass.get(type);
			classToType.remove(renderClass);
			if (renderClass == null) {
				logger.warn("unmapped type, no class found: " + type);
				continue;
			}
			renderClasses.add(renderClass);
		}
		if (!classToType.isEmpty()) {
			for (Entry<RenderClass, String> entry : classToType.entrySet()) {
				logger.warn(
						"unmapped class, not in order: " + entry.getValue());
			}
		}

		renderClassMap = new TIntObjectHashMap<>();
		for (RenderClass renderClass : renderClasses) {
			renderClassMap.put(renderClass.classId, renderClass);
		}

		reloadVisibility();
	}

	private void addRule(Rule rule, String type, int typeId, LabelContainer lc,
			float density)
	{
		LabelClass labelClass = new LabelClass(lc.getType(), lc.getLabel(), 1,
				density);

		int classId = idFactory++;
		RenderClass renderClass = new RenderClass(classId, typeId,
				rule.getMinZoom(), rule.getMaxZoom(), labelClass);

		logger.warn("Mapping type '" + type + " (" + typeId + ")' to class '"
				+ classId + "'");

		typeToClass.put(type, renderClass);
		classToType.put(renderClass, type);
		typeToClassId.put(type, classId);
		if (typeId >= 0) {
			typeIdToClassId.put(typeId, classId);
		}
	}

	public TIntHashSet getAllClassIds()
	{
		TIntHashSet set = new TIntHashSet();
		for (RenderClass renderClass : renderClasses) {
			set.add(renderClass.classId);
		}
		return set;
	}

	public TIntList getRelevantClassIds(int zoom)
	{
		TIntList ids = new TIntArrayList();
		for (RenderClass renderClass : renderClasses) {
			if (zoom >= renderClass.minZoom && zoom <= renderClass.maxZoom
					&& enabledIds.contains(renderClass.classId)) {
				ids.add(renderClass.classId);
			}
		}
		return ids;
	}

	public TIntList getRelevantTypeIds(int zoom)
	{
		TIntList ids = new TIntArrayList();
		for (RenderClass renderClass : renderClasses) {
			if (zoom >= renderClass.minZoom && zoom <= renderClass.maxZoom
					&& enabledIds.contains(renderClass.classId)) {
				if (renderClass.typeId >= 0) {
					ids.add(renderClass.typeId);
				}
			}
		}
		return ids;
	}

	public int getClassIdForTypeId(int typeId)
	{
		return typeIdToClassId.get(typeId);
	}

	public RenderClass get(int id)
	{
		return renderClassMap.get(id);
	}

	public boolean areHousenumbersRelevant(int zoom)
	{
		int classId = typeToClassId.get(Categories.TYPE_NAME_HOUSENUMBERS);
		RenderClass renderClass = renderClassMap.get(classId);
		return zoom >= renderClass.minZoom && zoom <= renderClass.maxZoom
				&& enabledIds.contains(renderClass.classId);
	}

	public int getHousenumberClassId()
	{
		return typeToClassId.get(Categories.TYPE_NAME_HOUSENUMBERS);
	}

	public static final String[] places = new String[] { "city", "town",
			"village", "hamlet", "suburb", "borough", "quarter",
			"neighborhood" };

	private void reloadVisibility()
	{
		configureAll();
	}

	private void configureMinimal()
	{
		for (String place : places) {
			int id = typeToClassId.get(place);
			enabledIds.add(id);
		}
	}

	private void configureAll()
	{
		TIntSet other = new TIntHashSet();
		other.addAll(typeToClassId.valueCollection());
		other.remove(typeToClassId.get(Categories.TYPE_NAME_HOUSENUMBERS));

		for (String place : places) {
			int id = typeToClassId.get(place);
			enabledIds.add(id);
			other.remove(id);
		}

		Categories categories = Categories.getLabelInstance();
		for (Group group : categories.getGroups()) {
			for (Category category : group.getChildren()) {
				boolean enabled = true;
				if (category instanceof DatabaseCategory) {
					DatabaseCategory dc = (DatabaseCategory) category;
					for (String type : dc.getIdentifiers()) {
						int id = typeToClassId.get(type);
						if (id < 0) {
							logger.warn("No id found for '" + type + "'");
							continue;
						}
						other.remove(id);
						if (enabled) {
							enabledIds.add(id);
						}
					}
				} else if (category instanceof MapfileCategory) {
					// MapfileCategory mc = (MapfileCategory) category;
					if (enabled) {
						int id = typeToClassId
								.get(Categories.TYPE_NAME_HOUSENUMBERS);
						enabledIds.add(id);
					}
				}
			}
		}

		boolean addOther = false;
		if (addOther) {
			enabledIds.addAll(other);
		}
	}

}
