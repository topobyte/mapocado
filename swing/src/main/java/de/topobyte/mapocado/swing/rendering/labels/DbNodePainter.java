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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.infomatiq.jsi.Rectangle;
import com.slimjars.dist.gnu.trove.iterator.TIntIterator;
import com.slimjars.dist.gnu.trove.list.TIntList;
import com.slimjars.dist.gnu.trove.map.TIntObjectMap;
import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.adt.geo.BBox;
import de.topobyte.jeography.core.mapwindow.TileMapWindow;
import de.topobyte.jeography.viewer.core.PaintListener;
import de.topobyte.jsi.intersectiontester.RTreeIntersectionTester;
import de.topobyte.jsi.intersectiontester.RectangleIntersectionTester;
import de.topobyte.jsijts.JsiAndJts;
import de.topobyte.luqe.iface.IConnection;
import de.topobyte.luqe.iface.QueryException;
import de.topobyte.luqe.jdbc.JdbcConnection;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.TextNode;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.rendering.RenderingLogic;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.PngSymbol;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.RenderElementComparable;
import de.topobyte.mapocado.swing.rendering.MapRenderConfig;
import de.topobyte.mapocado.swing.rendering.RuleShapeMap;
import de.topobyte.mercatorcoordinates.GeoConv;
import de.topobyte.misc.util.TimeCounter;
import de.topobyte.misc.util.TimeUtil;
import de.topobyte.nomioc.luqe.dao.Dao;
import de.topobyte.nomioc.luqe.model.SqLabel;
import de.topobyte.nomioc.luqe.model.SqPoiType;
import de.topobyte.sqlitespatial.spatialindex.access.SpatialIndex;

public class DbNodePainter implements PaintListener, NodePainter
{

	final static Logger logger = LoggerFactory.getLogger(DbNodePainter.class);

	private Connection connection;
	private Mapfile mapfile;
	private RenderConfig renderConfig;

	private boolean enabled = true;
	private boolean drawBorder = false;

	private Set<String> symbolClasses = new HashSet<>();

	private float userScaleFactor = 1;
	private float tileScaleFactor = 1;
	private float combinedScaleFactor = 1;

	private SpatialIndex spatialIndex;

	public DbNodePainter(Connection connection, Mapfile mapfile,
			MapRenderConfig renderConfig)
	{
		this.mapfile = mapfile;
		setup(connection, renderConfig);
	}

	public void setup(Connection connection, MapRenderConfig mapRenderConfig)
	{
		this.connection = connection;

		renderConfig = new RenderConfig(mapRenderConfig, connection);

		try {
			JdbcConnection db = new JdbcConnection(connection);
			spatialIndex = new SpatialIndex(db, "si_pois");
		} catch (QueryException e) {
			logger.error("Error while opening poi spatial index", e);
		}

		for (ObjectClass objectClass : mapRenderConfig.getClasses()) {
			for (RenderElement element : objectClass.elements) {
				if (element instanceof PngSymbol) {
					String id = objectClass.getId();
					symbolClasses.add(id);
				}
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	public void setDrawBorder(boolean drawBorder)
	{
		this.drawBorder = drawBorder;
	}

	public boolean isDrawBorder()
	{
		return drawBorder;
	}

	public void setUserScaleFactor(float userScaleFactor)
	{
		this.userScaleFactor = userScaleFactor;
		calculateCombinedScaleFactor();
	}

	public float getUserScaleFactor()
	{
		return userScaleFactor;
	}

	public void setTileScaleFactor(float tileScaleFactor)
	{
		this.tileScaleFactor = tileScaleFactor;
		calculateCombinedScaleFactor();
	}

	private void calculateCombinedScaleFactor()
	{
		combinedScaleFactor = userScaleFactor * tileScaleFactor;
	}

	@Override
	public void onPaint(TileMapWindow mapWindow, Graphics g)
	{
		if (!enabled) {
			return;
		}

		// red rectangle around everything
		if (drawBorder) {
			int width = mapWindow.getWidth();
			int height = mapWindow.getHeight();
			g.setColor(Color.RED);
			g.drawRect(10, 10, width - 20, height - 20);
		}

		BBox bbox = mapWindow.getBoundingBox();

		int zoom = mapWindow.getZoomLevel();
		// System.out.println(String.format("current window: %d, %s", zoom,
		// bbox.toString()));

		RuleShapeMap<RenderElement, Node> ruleShapeSetNodesSymbols = new RuleShapeMap<>();
		RuleShapeMap<RenderElement, Node> ruleShapeSetNodesCaptions = new RuleShapeMap<>();

		TIntList dbIds = renderConfig.getRelevantTypeIds(zoom);
		TIntList classIds = renderConfig.getRelevantClassIds(zoom);

		try {
			executeQuery(bbox, dbIds, zoom, ruleShapeSetNodesCaptions,
					ruleShapeSetNodesSymbols);
		} catch (QueryException e) {
			logger.warn("Error while executing labels query", e);
		}

		render(classIds, ruleShapeSetNodesSymbols, ruleShapeSetNodesCaptions,
				(Graphics2D) g, mapWindow);
	}

	private void render(TIntList classIds,
			RuleShapeMap<RenderElement, Node> ruleToSymbols,
			RuleShapeMap<RenderElement, Node> ruleToCaptions, Graphics2D g,
			TileMapWindow mapWindow)
	{
		// Map<RenderElement, List<Node>> ruleToListOfSymbol = ruleToSymbols
		// .getRuleToElements();
		Map<RenderElement, List<Node>> ruleToListOfCaption = ruleToCaptions
				.getRuleToElements();

		// Set<RenderElement> rulesSymbols = ruleToListOfSymbol.keySet();
		Set<RenderElement> rulesCaptions = ruleToListOfCaption.keySet();

		List<RenderElement> ruleListCaptions = new ArrayList<>(rulesCaptions);
		Collections.sort(ruleListCaptions, new RenderElementComparable());

		RectangleIntersectionTester tester = new RTreeIntersectionTester();

		TimeCounter tc = new TimeCounter(5);
		TimeUtil.time("caption:render");

		BBox bbox = mapWindow.getBoundingBox();

		BBox storageBox = new BBox(
				GeoConv.mercatorFromLongitude(bbox.getLon1()),
				GeoConv.mercatorFromLatitude(bbox.getLat1()),
				GeoConv.mercatorFromLongitude(bbox.getLon2()),
				GeoConv.mercatorFromLatitude(bbox.getLat2()));

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		AffineTransform backup = g.getTransform();

		TIntIterator iterator = classIds.iterator();
		while (iterator.hasNext()) {
			int id = iterator.next();
			List<SqLabel> labels = candidates.get(id);
			if (labels == null) {
				continue;
			}
			renderLabels(g, backup, labels, storageBox, mapWindow, tester, tc);
		}

		g.setTransform(backup);

		TimeUtil.time("caption:render", "caption rendering: %d");
		// System.out.println(String.format("stringWidth: %6d",
		// tc.getTotal(0)));
		// System.out.println(String.format("isFree: %6d",
		// tc.getTotal(1)));
		// System.out.println(String.format("glyphVector: %6d",
		// tc.getTotal(2)));
		// System.out.println(String.format("draw outline: %6d",
		// tc.getTotal(3)));
		// System.out.println(String.format("draw inner: %6d",
		// tc.getTotal(4)));
	}

	private void renderLabels(Graphics2D g, AffineTransform backup,
			List<SqLabel> labels, BBox storageBox, TileMapWindow mapWindow,
			RectangleIntersectionTester tester, TimeCounter tc)
	{
		for (SqLabel label : labels) {
			String name = label.getName();
			if (name == null) {
				continue;
			}
			if (!contains(storageBox, label.getX(), label.getY())) {
				continue;
			}

			double zoom = mapWindow.getZoom();
			double mx = Mercator.getX(label.getX(), zoom);
			double my = Mercator.getY(label.getY(), zoom);

			float x = (float) mapWindow.mercatorToX(mx);
			float y = (float) mapWindow.mercatorToY(my);

			// int bx = Math.round(x - label.width / 2);
			// int by = Math.round(y + labelClass.dy);
			int classId = renderConfig.getClassIdForTypeId(label.getType());

			RenderClass renderClass = renderConfig.get(classId);
			if (renderClass == null) {
				continue;
			}
			LabelClass lc = renderClass.labelClass;

			FontMetrics fontMetrics = lc.fontMetrics;

			float dy = RenderingLogic.scale(lc.dy, combinedScaleFactor);

			int stringWidth = fontMetrics.stringWidth(name);
			int stringHeight = fontMetrics.getHeight();
			Envelope envelope = new Envelope(x - stringWidth / 2,
					x + stringWidth / 2, y + dy - stringHeight, y + dy);
			// System.out.println(String.format("x : %f : %f y : %f : %f", x
			// - stringWidth / 2, x + stringWidth / 2, y + caption.getDy()
			// - stringHeight, y + caption.getDy()));
			// System.out.println(envelope);

			tc.start(1);
			Rectangle rectangle = JsiAndJts.toRectangle(envelope);
			boolean isFree = tester.isFree(rectangle);
			tc.stop(1);
			if (!isFree) {
				continue;
			}

			tester.add(rectangle, false);

			renderText(g, backup, lc.font, name, stringWidth, x, y, dy,
					stringHeight, lc.fg, lc.bg, lc.strokeWidth, tc);
		}
	}

	private boolean contains(BBox box, double lon, double lat)
	{
		return lon >= box.getLon1() && lon <= box.getLon2()
				&& lat >= box.getLat2() && lat <= box.getLat1();
	}

	private void renderText(Graphics2D g, AffineTransform backup, Font font,
			String value, int stringWidth, double x, double y, float dy,
			float sdy, Color fg, Color bg, float strokeWidth, TimeCounter tc)
	{
		/*
		 * Here are two implementations for the outline:
		 * 
		 * 1) java2d font outline -> stroke
		 * 
		 * 2) fast, simple, non-strokeWidth honoring method
		 */

		tc.start(2);
		/* this is for implementation 1) get font shape */
		FontRenderContext frc = g.getFontRenderContext();
		GlyphVector glyphVector = font.createGlyphVector(frc, value);
		Shape outline = glyphVector.getOutline();
		tc.stop(2);

		/* transformation */
		AffineTransform transform = new AffineTransform(backup);
		transform.translate(x - stringWidth / 2, y + dy + sdy);
		// + stringHeight / 2.0
		g.setTransform(transform);

		/* execute drawing */
		g.setColor(bg);
		BasicStroke outlineStroke = new BasicStroke(strokeWidth,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		g.setStroke(outlineStroke);

		tc.start(3);
		/* this is for implementation 2) */
		// g.drawString(value, 1, 0);
		// g.drawString(value, -1, 0);
		// g.drawString(value, 0, 1);
		// g.drawString(value, 0, -1);
		// g.drawString(value, 1, 1);
		// g.drawString(value, -1, -1);
		// g.drawString(value, -1, 1);
		// g.drawString(value, 1, -1);

		/* this is for implementation 1) again. */
		g.draw(outline);
		tc.stop(3);

		/*
		 * here is the inside
		 */
		tc.start(4);
		g.setColor(fg);
		// g.drawString(value, 0, 0);
		g.fill(outline);
		tc.stop(4);
	}

	// This map stores all currently known label candidates. It maps from some
	// label-class identifier to the set of candidates of that type.
	protected TIntObjectHashMap<List<SqLabel>> candidates = new TIntObjectHashMap<>();

	private void executeQuery(BBox request, TIntList dbIds, int zoom,
			RuleShapeMap<RenderElement, Node> ruleShapeSetNodesCaptions,
			RuleShapeMap<RenderElement, Node> ruleShapeSetNodesSymbols)
			throws QueryException
	{
		final TIntObjectMap<String> typeNames = new TIntObjectHashMap<>();
		IConnection connex = new JdbcConnection(connection);
		List<SqPoiType> types = Dao.getTypes(connex);
		for (SqPoiType type : types) {
			typeNames.put(type.getId(), type.getName());
		}

		List<SqLabel> labels = null;
		try {
			if (zoom > 12) {
				labels = Dao.getLabels(connex, spatialIndex, request, dbIds);
			} else {
				labels = Dao.getLabels(connex, request, dbIds);
			}
		} catch (QueryException e) {
			logger.error("Error while fetching labels", e);
			return;
		}

		TIntObjectHashMap<List<SqLabel>> newCandidates = new TIntObjectHashMap<>();
		for (SqLabel label : labels) {
			int typeId = label.getType();
			int classId = renderConfig.getClassIdForTypeId(typeId);
			List<SqLabel> values = newCandidates.get(classId);
			if (values == null) {
				values = new ArrayList<>();
				newCandidates.put(classId, values);
			}
			values.add(label);
		}
		candidates = newCandidates;

		if (renderConfig.areHousenumbersRelevant(zoom)) {
			DiskTree<TextNode> treeHousenumbers = mapfile.getTreeHousenumbers();

			BoundingBox rectRequest = new BoundingBox(request.getLon1(),
					request.getLon2(), request.getLat1(), request.getLat2(),
					true);
			try {
				List<TextNode> housenumbers = treeHousenumbers
						.intersectionQuery(rectRequest);
				logger.info("Number of housenumbers: " + housenumbers.size());
				int classId = renderConfig.getHousenumberClassId();

				List<SqLabel> numbers = new ArrayList<>();
				candidates.put(classId, numbers);
				for (TextNode number : housenumbers) {
					Coordinate point = number.getPoint();
					SqLabel label = new SqLabel();
					label.setX(point.getX());
					label.setY(point.getY());
					label.setName(number.getText());
					label.setType(classId);
					numbers.add(label);
				}
			} catch (IOException e) {
				logger.error("Error while querying mapfile for housenumbers",
						e);
			}
		}
	}
}
