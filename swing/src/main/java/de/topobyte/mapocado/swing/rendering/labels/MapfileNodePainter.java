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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Envelope;

import com.infomatiq.jsi.Rectangle;

import de.topobyte.adt.geo.BBox;
import de.topobyte.jeography.core.mapwindow.TileMapWindow;
import de.topobyte.jeography.viewer.core.PaintListener;
import de.topobyte.jsi.intersectiontester.RTreeIntersectionTester;
import de.topobyte.jsi.intersectiontester.RectangleIntersectionTester;
import de.topobyte.jsijts.JsiAndJts;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.rendering.RenderingLogic;
import de.topobyte.mapocado.styles.ElementResolver;
import de.topobyte.mapocado.styles.classes.element.BvgSymbol;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.PngSymbol;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.RenderElementComparable;
import de.topobyte.mapocado.styles.classes.element.slim.CaptionSlim;
import de.topobyte.mapocado.swing.rendering.Conversion;
import de.topobyte.mapocado.swing.rendering.MapRenderConfig;
import de.topobyte.mapocado.swing.rendering.RuleShapeMap;
import de.topobyte.misc.util.TimeCounter;
import de.topobyte.misc.util.TimeUtil;

public class MapfileNodePainter implements PaintListener, NodePainter
{

	private Mapfile mapfile;
	private ElementResolver elementResolver;

	private boolean enabled = true;
	private boolean drawBorder = false;

	private Set<String> symbolClasses = new HashSet<>();

	private float userScaleFactor = 1;
	private float tileScaleFactor = 1;
	private float combinedScaleFactor = 1;

	public MapfileNodePainter(Mapfile mapfile, MapRenderConfig renderConfig)
	{
		setup(mapfile, renderConfig);
	}

	public void setup(Mapfile mapfile, MapRenderConfig renderConfig)
	{
		this.mapfile = mapfile;
		this.elementResolver = renderConfig
				.getResolver(mapfile.getMetadata().getPoolForRefs());

		for (ObjectClass objectClass : renderConfig.getClasses()) {
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

		BoundingBox request = new BoundingBox(bbox.getLon1(), bbox.getLon2(),
				bbox.getLat2(), bbox.getLat1(), true);

		// Increase the size of the request rectangle at the bottom to include
		// labels whose node is out of the request, but should be displayed
		// because since it will be shifted up it may be well visible
		double off = 20;
		int offStorage = (int) Math.ceil(Mercator
				.getLengthStorageUnitsDefault(off, mapWindow.getZoomLevel()));
		request = new BoundingBox(request.getMinX(), request.getMaxX(),
				request.getMinY(), request.getMaxY() + offStorage);

		RuleShapeMap<RenderElement, Node> ruleShapeSetNodesSymbols = new RuleShapeMap<>();
		RuleShapeMap<RenderElement, Node> ruleShapeSetNodesCaptions = new RuleShapeMap<>();

		executeQuery(request, zoom, ruleShapeSetNodesCaptions,
				ruleShapeSetNodesSymbols);

		render(ruleShapeSetNodesSymbols, ruleShapeSetNodesCaptions,
				(Graphics2D) g, mapWindow);
	}

	private void render(RuleShapeMap<RenderElement, Node> ruleToSymbols,
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
		for (RenderElement rule : ruleListCaptions) {
			CaptionSlim caption = (CaptionSlim) rule;
			renderCaption(mapWindow, g, caption, ruleToListOfCaption, tester,
					tc);
		}
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

	private void renderCaption(TileMapWindow mapWindow, Graphics2D g,
			CaptionSlim caption,
			Map<RenderElement, List<Node>> ruleToListOfCaption,
			RectangleIntersectionTester tester, TimeCounter tc)
	{
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		String fontFamily = Conversion.getFontFamily(caption.getFontFamily());
		int style = Conversion.getFontStyle(caption.getFontStyle());

		float fontSize = caption.getFontSize();
		fontSize = RenderingLogic.scale(fontSize, combinedScaleFactor);
		Font font = new Font(fontFamily, style, Math.round(fontSize));
		FontMetrics fontMetrics = g.getFontMetrics(font);
		g.setFont(font);
		// int ascent = fontMetrics.getAscent();

		AffineTransform backup = g.getTransform();

		List<Node> nodes = ruleToListOfCaption.get(caption);
		for (Node node : nodes) {
			Coordinate point = node.getPoint();

			int mx = Mercator.getX(point.getX(), mapWindow.getZoomLevel());
			int my = Mercator.getY(point.getY(), mapWindow.getZoomLevel());

			double x = mapWindow.mercatorToX(mx);
			double y = mapWindow.mercatorToY(my);

			int key = caption.getValK();
			String value = node.getTags().get(key);
			if (value == null) {
				continue;
			}

			// if this node has a symbol, add an offset
			float sdy = 0;
			if (!caption.hasDeltaY() && node.hasSymbol()) {
				sdy = -10;
			}
			sdy = RenderingLogic.scale(sdy, combinedScaleFactor);

			float dy = caption.getDy();
			dy = RenderingLogic.scale(dy, combinedScaleFactor);

			tc.start(0);
			int stringWidth = fontMetrics.stringWidth(value);
			tc.stop(0);
			int stringHeight = fontMetrics.getHeight();
			Envelope envelope = new Envelope(x - stringWidth / 2,
					x + stringWidth / 2, y + dy + sdy - stringHeight,
					y + dy + sdy);
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
			g.setColor(Conversion.getColor(caption.getStroke()));
			BasicStroke outlineStroke = new BasicStroke(
					caption.getStrokeWidth(), BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND);
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
			g.setColor(Conversion.getColor(caption.getFill()));
			// g.drawString(value, 0, 0);
			g.fill(outline);
			tc.stop(4);
		}

		g.setTransform(backup);
	}

	private void executeQuery(BoundingBox request, int zoom,
			RuleShapeMap<RenderElement, Node> ruleShapeSetNodesCaptions,
			RuleShapeMap<RenderElement, Node> ruleShapeSetNodesSymbols)
	{
		IntervalTree<Integer, DiskTree<Node>> treeNodes = mapfile
				.getTreeNodes();
		List<DiskTree<Node>> trees = treeNodes.getObjects(zoom);

		for (DiskTree<Node> tree : trees) {
			try {
				List<Node> nodes = tree.intersectionQuery(request);
				// System.out.println("#results: " + nodes.size());
				List<RenderElement> elements = new ArrayList<>();
				for (Node node : nodes) {
					elements.clear();
					elementResolver.lookupElements(node, zoom, elements);
					if (elements.size() == 0) {
						continue;
					}

					for (RenderElement element : elements) {
						if (element instanceof PngSymbol) {
							ruleShapeSetNodesSymbols.put(element, node);
							node.setHasSymbol(true);
						} else if (element instanceof BvgSymbol) {
							ruleShapeSetNodesSymbols.put(element, node);
							node.setHasSymbol(true);
						} else if (element instanceof CaptionSlim) {
							ruleShapeSetNodesCaptions.put(element, node);
						}
					}
				}
			} catch (IOException e) {
				System.out.println(
						"IOException in NodePainter query: " + e.getMessage());
			}
		}
	}
}
