package de.topobyte.mapocado.swing.rendering;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.adt.geo.BBox;
import de.topobyte.bvg.BvgAwtPainter;
import de.topobyte.bvg.BvgIO;
import de.topobyte.bvg.BvgImage;
import de.topobyte.mapocado.mapformat.CoordinateTransformer;
import de.topobyte.mapocado.mapformat.LengthTransformer;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.rendering.Clipping;
import de.topobyte.mapocado.rendering.RenderingLogic;
import de.topobyte.mapocado.rendering.pathtext.PathLabeller;
import de.topobyte.mapocado.rendering.text.TextIntersectionChecker;
import de.topobyte.mapocado.rendering.text.TextIntersectionCheckerTree;
import de.topobyte.mapocado.styles.ElementResolver;
import de.topobyte.mapocado.styles.MapStyleDataProvider;
import de.topobyte.mapocado.styles.classes.element.Area;
import de.topobyte.mapocado.styles.classes.element.BvgLineSymbol;
import de.topobyte.mapocado.styles.classes.element.BvgSymbol;
import de.topobyte.mapocado.styles.classes.element.Circle;
import de.topobyte.mapocado.styles.classes.element.Line;
import de.topobyte.mapocado.styles.classes.element.LineSymbol;
import de.topobyte.mapocado.styles.classes.element.PngLineSymbol;
import de.topobyte.mapocado.styles.classes.element.PngSymbol;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.RenderElementComparable;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;
import de.topobyte.mapocado.swing.rendering.items.NodeItem;
import de.topobyte.mapocado.swing.rendering.items.RelationItem;
import de.topobyte.mapocado.swing.rendering.items.RenderItem;
import de.topobyte.mapocado.swing.rendering.items.WayItem;
import de.topobyte.mapocado.swing.rendering.linesymbol.AwtLineSymbolRenderer;
import de.topobyte.mapocado.swing.rendering.linesymbol.BvgLineSymbolRenderer;
import de.topobyte.mapocado.swing.rendering.linesymbol.PngLineSymbolRenderer;
import de.topobyte.mapocado.swing.rendering.pathtext.AwtPathLabeller;
import de.topobyte.misc.util.TimeCounter;

public class MapImageRenderer
{
	final static Logger logger = LoggerFactory
			.getLogger(MapImageRenderer.class);

	private Mapfile mapfile;
	private Color backgroundColor;
	private MapStyleDataProvider styleDataProvider;
	protected ElementResolver elementResolver;

	protected float combinedScaleFactor = 1;

	public MapImageRenderer(Mapfile mapfile, MapRenderConfig renderConfig)
	{
		this.mapfile = mapfile;
		StringPool poolForRefs = mapfile.getMetadata().getPoolForRefs();
		this.elementResolver = renderConfig.getResolver(poolForRefs);
		backgroundColor = Conversion
				.getColor(renderConfig.getBackgroundColor());
		styleDataProvider = renderConfig.getStyleDataProvider();
	}

	public Mapfile getMapfile()
	{
		return mapfile;
	}

	protected void create(Graphics2D g, LengthTransformer transformer,
			BBox bboxRequestNode, BBox bboxRequestWay, int zoom, int width,
			int height, Clipping clipping, Clipping hitTest)
	{
		IntervalTree<Integer, DiskTree<Node>> treeNodes = mapfile
				.getTreeNodes();
		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();
		IntervalTree<Integer, DiskTree<Relation>> treeRelations = mapfile
				.getTreeRelations();

		RuleShapeMap<RenderElement, Node> ruleShapeSetNodesSymbols = new RuleShapeMap<>();
		RuleShapeMap<RenderElement, NodeItem> ruleShapeSetNodes = new RuleShapeMap<>();
		RuleShapeMap<RenderElement, WayItem> ruleShapeSetWays = new RuleShapeMap<>();
		RuleShapeMap<RenderElement, RelationItem> ruleShapeSetRelations = new RuleShapeMap<>();

		BoundingBox rectRequestWay = new BoundingBox(bboxRequestWay.getLon1(),
				bboxRequestWay.getLon2(), bboxRequestWay.getLat1(),
				bboxRequestWay.getLat2(), true);

		BoundingBox rectRequestNode = new BoundingBox(bboxRequestNode.getLon1(),
				bboxRequestNode.getLon2(), bboxRequestNode.getLat1(),
				bboxRequestNode.getLat2(), true);

		/*
		 * query and gather data
		 */

		long startQuery = System.currentTimeMillis();
		List<Node> nodes;
		try {
			List<DiskTree<Node>> nodeTrees = treeNodes.getObjects(zoom);
			for (DiskTree<Node> tree : nodeTrees) {
				nodes = tree.intersectionQuery(rectRequestNode);
				logger.debug("number of nodes: " + nodes.size());
				for (Node node : nodes) {
					handle(node, transformer, ruleShapeSetNodes,
							ruleShapeSetNodesSymbols, zoom);
				}
			}

			List<DiskTree<Way>> wayTrees = treeWays.getObjects(zoom);
			for (DiskTree<Way> tree : wayTrees) {
				List<Way> ways = tree.intersectionQuery(rectRequestWay);
				logger.debug("number of ways: " + ways.size());
				for (Way way : ways) {
					handle(way, transformer, ruleShapeSetWays, zoom, clipping);
				}
			}

			List<DiskTree<Relation>> relationTrees = treeRelations
					.getObjects(zoom);
			for (DiskTree<Relation> tree : relationTrees) {
				List<Relation> relations = tree
						.intersectionQuery(rectRequestWay);
				logger.debug("number of relations: " + relations.size());
				for (Relation relation : relations) {
					handle(relation, transformer, ruleShapeSetRelations, zoom);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		long endQuery = System.currentTimeMillis();
		long timeQuery = endQuery - startQuery;
		logger.debug("time for queries: " + timeQuery);

		Map<RenderElement, List<NodeItem>> ruleToNodes = ruleShapeSetNodes
				.getRuleToElements();

		Map<RenderElement, List<Node>> ruleToSymbols = ruleShapeSetNodesSymbols
				.getRuleToElements();

		Map<RenderElement, List<WayItem>> ruleToWays = ruleShapeSetWays
				.getRuleToElements();

		Map<RenderElement, List<RelationItem>> ruleToRelation = ruleShapeSetRelations
				.getRuleToElements();

		/*
		 * render
		 */

		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);

		long startRender = System.currentTimeMillis();
		render(g, zoom, hitTest, ruleToWays, ruleToNodes, ruleToRelation,
				ruleToSymbols, transformer);
		long endRender = System.currentTimeMillis();
		long timeRender = endRender - startRender;
		logger.debug("time for rendering: " + timeRender);
	}

	protected void handle(Node node, CoordinateTransformer transformer,
			RuleShapeMap<RenderElement, NodeItem> ruleShapeSetNodes,
			RuleShapeMap<RenderElement, Node> ruleShapeSetNodesSymbols,
			int zoom)
	{
		List<RenderElement> elements = new ArrayList<>();
		elementResolver.lookupElements(node, zoom, elements);
		if (elements.size() == 0) {
			return;
		}

		for (RenderElement element : elements) {
			if (element instanceof PngSymbol) {
				ruleShapeSetNodesSymbols.put(element, node);
			} else if (element instanceof BvgSymbol) {
				ruleShapeSetNodesSymbols.put(element, node);
			} else if (element instanceof Circle) {
				Coordinate point = node.getPoint();
				float x = transformer.getX(point.getX());
				float y = transformer.getY(point.getY());
				NodeItem nodeItem = new NodeItem(x, y);
				ruleShapeSetNodes.put(element, nodeItem);
			}
		}
	}

	protected void handle(Way way, CoordinateTransformer transformer,
			RuleShapeMap<RenderElement, WayItem> ruleShapeSetWays, int zoom,
			Clipping clipping)
	{
		List<RenderElement> elements = new ArrayList<>();
		elementResolver.lookupElements(way, zoom, elements);
		if (elements.size() == 0) {
			return;
		}
		Linestring string = way.getString();
		Path2D path = string.isClosed()
				? GeometryTransformation.getPath(string, transformer)
				: GeometryTransformation.getClippedPath(string, transformer,
						clipping);
		WayItem wayItem = new WayItem(way, path, string.isClosed());

		for (RenderElement element : elements) {
			ruleShapeSetWays.put(element, wayItem);
		}
	}

	protected void handle(Relation relation, CoordinateTransformer transformer,
			RuleShapeMap<RenderElement, RelationItem> ruleShapeSetRelations,
			int zoom)
	{
		List<RenderElement> elements = new ArrayList<>();
		elementResolver.lookupElements(relation, zoom, elements);
		if (elements.size() == 0) {
			return;
		}
		Multipolygon polygon = relation.getPolygon();
		java.awt.geom.Area area = GeometryTransformation.toShape(polygon,
				transformer);
		RelationItem relationItem = new RelationItem(relation, area);

		for (RenderElement element : elements) {
			ruleShapeSetRelations.put(element, relationItem);
		}
	}

	private Map<String, Long> times = new HashMap<>();

	private void time(String key)
	{
		times.put(key, System.currentTimeMillis());
	}

	private void time(String key, String message)
	{
		long stop = System.currentTimeMillis();
		long start = times.get(key);
		long interval = stop - start;
		logger.debug(String.format(message, interval));
	}

	private static int DEFAULT_JOIN = BasicStroke.JOIN_ROUND;

	private void render(Graphics2D g, int zoom, Clipping hitTest,
			Map<RenderElement, List<WayItem>> ruleToWays,
			Map<RenderElement, List<NodeItem>> ruleToNodes,
			Map<RenderElement, List<RelationItem>> ruleToRelations,
			Map<RenderElement, List<Node>> ruleToSymbols,
			LengthTransformer transformer)
	{
		Map<RenderElement, List<RenderItem>> ruleToItems = RuleShapeMap
				.merge(ruleToWays, ruleToRelations, ruleToNodes);

		Set<RenderElement> rules = ruleToItems.keySet();
		// logger.debug("WAY RULES: " + rules);
		List<RenderElement> ruleList = new ArrayList<>(rules);
		Collections.sort(ruleList, new RenderElementComparable());

		List<PathTextSlim> pathRules = new ArrayList<>();

		TimeCounter tc = new TimeCounter(3);

		// g.setRenderingHints(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_OFF);
		time("r1");
		for (RenderElement rule : ruleList) {
			if (rule instanceof Line) {
				tc.start(0);
				Line line = (Line) rule;
				renderLine(g, zoom, line, ruleToItems);
				tc.stop(0);
			} else if (rule instanceof Area) {
				tc.start(1);
				Area area = (Area) rule;
				renderArea(g, zoom, area, ruleToItems);
				tc.stop(1);
			} else if (rule instanceof PathTextSlim) {
				PathTextSlim pathText = (PathTextSlim) rule;
				pathRules.add(pathText);
			} else if (rule instanceof Circle) {
				tc.start(2);
				Circle circle = (Circle) rule;
				renderCircle(g, zoom, circle, ruleToItems);
				tc.start(2);
			} else if (rule instanceof BvgLineSymbol) {
				BvgLineSymbol lineSymbol = (BvgLineSymbol) rule;
				renderLineSymbol(g, zoom, lineSymbol, ruleToWays, transformer);
			} else if (rule instanceof PngLineSymbol) {
				PngLineSymbol lineSymbol = (PngLineSymbol) rule;
				renderLineSymbol(g, zoom, lineSymbol, ruleToWays, transformer);
			} else {
				// logger.debug(rule.getClass() + ": " + rule);
			}
		}
		time("r1", "time for line/area elements: %d");
		logger.debug("time for lines: " + tc.getTotal(0));
		logger.debug("time for areas: " + tc.getTotal(1));
		logger.debug("time for circles: " + tc.getTotal(2));
		// g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);

		Set<RenderElement> symbolRules = ruleToSymbols.keySet();
		// logger.debug("NODE RULES: " + nodeRules);
		List<RenderElement> nodeRuleList = new ArrayList<>(symbolRules);
		Collections.sort(nodeRuleList, new RenderElementComparable());
		for (RenderElement rule : nodeRuleList) {
			if (rule instanceof PngSymbol) {
				// logger.debug("RULE: " + rule);
				PngSymbol symbol = (PngSymbol) rule;
				renderSymbol(g, symbol, ruleToSymbols, transformer);
			} else if (rule instanceof BvgSymbol) {
				BvgSymbol symbol = (BvgSymbol) rule;
				renderSymbol(g, symbol, ruleToSymbols, transformer);
			}
		}

		TextIntersectionChecker checker = new TextIntersectionCheckerTree();
		PathLabeller labeller = new AwtPathLabeller(g, checker, hitTest, zoom,
				transformer, combinedScaleFactor);

		time("rt");
		for (PathTextSlim pathText : pathRules) {
			labeller.setStyle(pathText);
			int valK = pathText.getValK();
			List<WayItem> ways = ruleToWays.get(pathText);
			if (ways == null) {
				continue;
			}
			for (WayItem way : ways) {
				TIntObjectHashMap<String> tags = way.getWay().getTags();

				String labelText = tags.get(valK);

				if (labelText == null) {
					continue;
				}
				labeller.renderPathText(way.getWay().getString(), labelText);
			}
		}
		logger.debug(
				"number of path texts: " + labeller.getNumberOfPathTexts());
		time("rt", "time for path texts: %d");
	}

	private void renderSymbol(Graphics2D g, PngSymbol symbol,
			Map<RenderElement, List<Node>> ruleToNodes,
			CoordinateTransformer transformer)
	{
		List<Node> nodes = ruleToNodes.get(symbol);
		// logger.debug("NODES: " + nodes);
		if (nodes == null) {
			return;
		}
		BufferedImage symbolImage = getPngSymbolImage(symbol.getSource());
		if (symbolImage == null) {
			logger.debug("ERROR: unable to retrieve image");
			return;
		}
		for (Node node : nodes) {
			float x = transformer.getX(node.getPoint().getX());
			float y = transformer.getY(node.getPoint().getY());
			// logger.debug(String.format("x, y: %d, %d", x, y));
			g.setColor(Color.BLACK);
			int imgX = (int) Math.round(x - symbolImage.getWidth() / 2.0);
			int imgY = (int) Math.round(y - symbolImage.getHeight() / 2.0);
			g.drawImage(symbolImage, imgX, imgY, null);
		}
	}

	private void renderSymbol(Graphics2D g, BvgSymbol symbol,
			Map<RenderElement, List<Node>> ruleToNodes,
			CoordinateTransformer transformer)
	{
		List<Node> nodes = ruleToNodes.get(symbol);
		// logger.debug("NODES: " + nodes);
		if (nodes == null) {
			return;
		}
		BvgImage symbolImage = getBvgSymbolImage(symbol.getSource());
		if (symbolImage == null) {
			logger.debug("ERROR: unable to retrieve image");
			return;
		}
		float height = symbol.getHeight();
		float width = (float) (height
				* (symbolImage.getWidth() / symbolImage.getHeight()));
		height = RenderingLogic.scale(height, combinedScaleFactor);
		width = RenderingLogic.scale(width, combinedScaleFactor);
		float scale = (float) (height / symbolImage.getHeight());
		for (Node node : nodes) {
			float x = transformer.getX(node.getPoint().getX());
			float y = transformer.getY(node.getPoint().getY());
			// logger.debug(String.format("x, y: %d, %d", x, y));
			g.setColor(Color.BLACK);
			float imgX = x - width / 2;
			float imgY = y - height / 2;
			BvgAwtPainter.draw(g, symbolImage, imgX, imgY, scale, scale);
		}
	}

	private void renderArea(Graphics2D g, int zoom, Area area,
			Map<RenderElement, List<RenderItem>> ruleToWays)
	{
		List<RenderItem> ways = ruleToWays.get(area);
		if (ways == null) {
			return;
		}

		if (area.getFill() != null || area.getSource() != null) {
			g.setColor(Conversion.getColor(area.getFill()));
			if (area.getSource() != null) {
				Paint texture = getTexture(area.getSource());
				g.setPaint(texture);
			}

			for (RenderItem way : ways) {
				if (!way.isClosed()) {
					continue;
				}
				Shape path = way.getShape();
				java.awt.geom.Area a = new java.awt.geom.Area(path);
				fillWay(g, a);
			}
		}

		if (area.getStroke() != null) {
			float strokeWidth = area.getStrokeWidth();
			strokeWidth = RenderingLogic.scaleStroke(strokeWidth, zoom);
			strokeWidth = RenderingLogic.scale(strokeWidth,
					combinedScaleFactor);
			g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
					DEFAULT_JOIN));
			g.setColor(Conversion.getColor(area.getStroke()));

			for (RenderItem way : ways) {
				if (!way.isClosed()) {
					continue;
				}
				Shape path = way.getShape();
				drawWay(g, path);
			}
		}
	}

	private void renderLine(Graphics2D g, int zoom, Line line,
			Map<RenderElement, List<RenderItem>> ruleToWays)
	{
		List<RenderItem> ways = ruleToWays.get(line);
		if (ways == null) {
			return;
		}

		int lineCap = Conversion.getLineCap(line.getCapType());
		float strokeWidth = line.getStrokeWidth();
		strokeWidth = RenderingLogic.scaleStroke(strokeWidth, zoom);
		strokeWidth = RenderingLogic.scale(strokeWidth, combinedScaleFactor);
		List<Float> dashArray = line.getDashArray();
		if (dashArray == null) {
			g.setStroke(new BasicStroke(strokeWidth, lineCap, DEFAULT_JOIN));
		} else {
			float[] dash = new float[dashArray.size()];
			for (int i = 0; i < dashArray.size(); i++) {
				dash[i] = RenderingLogic.scaleDash(dashArray.get(i), zoom);
			}
			g.setStroke(new BasicStroke(strokeWidth, lineCap, DEFAULT_JOIN,
					BasicStroke.JOIN_MITER, dash, 0f));
		}
		g.setColor(Conversion.getColor(line.getStroke()));

		for (RenderItem way : ways) {
			drawWay(g, way.getShape());
		}
	}

	private void renderCircle(Graphics2D g, int zoom, Circle circle,
			Map<RenderElement, List<RenderItem>> ruleToItems)
	{
		List<RenderItem> nodes = ruleToItems.get(circle);
		if (nodes == null) {
			return;
		}

		float strokeWidth = circle.getStrokeWidth();
		float radius = circle.getRadius();
		if (circle.isScaleRadius()) {
			strokeWidth = RenderingLogic.scaleStroke(strokeWidth, zoom);
			radius = RenderingLogic.scaleRadius(radius, zoom);
		}
		radius = RenderingLogic.scale(radius, combinedScaleFactor);
		strokeWidth = RenderingLogic.scale(strokeWidth, combinedScaleFactor);

		g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
				DEFAULT_JOIN));

		for (RenderItem node : nodes) {
			Point2D point = node.getPoint();
			int x = (int) Math.round(point.getX() - radius);
			int y = (int) Math.round(point.getY() - radius);
			int width = (int) Math.round(point.getX() + radius - x);
			int height = (int) Math.round(point.getY() + radius - y);
			g.setColor(Conversion.getColor(circle.getFill()));
			g.fillArc(x, y, width, height, 0, 360);
			g.setColor(Conversion.getColor(circle.getStroke()));
			g.drawArc(x, y, width, height, 0, 360);
		}
	}

	private void renderLineSymbol(Graphics2D g, int zoom,
			PngLineSymbol lineSymbol,
			Map<RenderElement, List<WayItem>> ruleToWays,
			LengthTransformer transformer)
	{
		BufferedImage symbolImage = getPngSymbolImage(lineSymbol.getSource());
		if (symbolImage == null) {
			logger.debug("ERROR: unable to retrieve image");
			return;
		}

		AwtLineSymbolRenderer<?> lineSymbolRenderer = new PngLineSymbolRenderer(
				symbolImage);

		int height = symbolImage.getHeight();
		int width = symbolImage.getWidth();

		List<WayItem> ways = ruleToWays.get(lineSymbol);
		if (ways == null) {
			return;
		}

		renderLineSymbol(g, lineSymbolRenderer, lineSymbol, transformer, zoom,
				height, width, ways);
	}

	private void renderLineSymbol(Graphics2D g, int zoom,
			BvgLineSymbol lineSymbol,
			Map<RenderElement, List<WayItem>> ruleToWays,
			LengthTransformer transformer)
	{
		BvgImage symbolImage = getBvgSymbolImage(lineSymbol.getSource());
		if (symbolImage == null) {
			logger.debug("ERROR: unable to retrieve image");
			return;
		}

		AwtLineSymbolRenderer<?> lineSymbolRenderer = new BvgLineSymbolRenderer(
				symbolImage);

		float width = lineSymbol.getWidth();
		float height = (float) (symbolImage.getHeight() / symbolImage.getWidth()
				* width);

		List<WayItem> ways = ruleToWays.get(lineSymbol);
		if (ways == null) {
			return;
		}

		renderLineSymbol(g, lineSymbolRenderer, lineSymbol, transformer, zoom,
				height, width, ways);
	}

	private void renderLineSymbol(Graphics2D g,
			AwtLineSymbolRenderer<?> lineSymbolRenderer, LineSymbol lineSymbol,
			LengthTransformer transformer, int zoom, float height, float width,
			List<WayItem> ways)
	{
		boolean repeat = lineSymbol.isRepeat();
		float repeatDistance = lineSymbol.getRepeatDistance();
		float offset = lineSymbol.getOffset();

		double symbolWidthStorage = transformer.getLengthStorageUnits(width,
				zoom);
		double offsetStorage = transformer.getLengthStorageUnits(offset, zoom);
		double repeatStorage = transformer.getLengthStorageUnits(repeatDistance,
				zoom);

		lineSymbolRenderer.init(g, transformer, height, symbolWidthStorage,
				offsetStorage, combinedScaleFactor);

		if (!repeat) {
			for (WayItem way : ways) {
				lineSymbolRenderer.renderLineSymbol(way.getWay().getString());
			}
		} else {
			for (WayItem way : ways) {
				lineSymbolRenderer.renderLineSymbol(way.getWay().getString(),
						repeatStorage);
			}
		}
	}

	private void drawWay(Graphics2D g, Shape way)
	{
		g.draw(way);
	}

	private void fillWay(Graphics2D g, java.awt.geom.Area area)
	{
		g.fill(area);
	}

	private ImageReader reader = ImageIO.getImageReadersByFormatName("png")
			.next();

	private BufferedImage readPngImage(InputStream input) throws IOException
	{
		ImageInputStream imageInput = null;
		try {
			imageInput = ImageIO.createImageInputStream(input);
			if (imageInput == null) {
				logger.debug("unable to read image");
				return null;
			}
			reader.setInput(imageInput, true);
			BufferedImage image = reader.read(0);
			reader.reset();
			return image;
		} catch (IOException e) {
			throw (e);
		} finally {
			if (imageInput != null) {
				imageInput.close();
			}
		}
	}

	private BvgImage readBvgImage(InputStream input) throws IOException
	{
		BvgImage image = BvgIO.read(input);
		return image;
	}

	private Map<String, Paint> textures = new HashMap<>();

	private Paint getTexture(String sourcePath)
	{
		// logger.debug(sourcePath);
		Paint paint = textures.get(sourcePath);
		if (paint != null) {
			return paint;
		}
		try {
			InputStream input = styleDataProvider.getTexture(sourcePath);
			BufferedImage image = readPngImage(input);
			if (image == null) {
				logger.warn("unable to open texture image: " + sourcePath);
				return null;
			}
			java.awt.Rectangle anchor = new java.awt.Rectangle(image.getWidth(),
					image.getHeight());
			paint = new TexturePaint(image, anchor);
			textures.put(sourcePath, paint);
			return paint;
		} catch (IOException e) {
			logger.debug("error while loading image: " + sourcePath);
			logger.debug("error message: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, BufferedImage> pngSymbolImages = new HashMap<>();

	private BufferedImage getPngSymbolImage(String sourcePath)
	{
		// logger.debug(sourcePath);
		BufferedImage image = pngSymbolImages.get(sourcePath);
		if (image != null) {
			return image;
		}
		try {
			InputStream input = styleDataProvider.getSymbol(sourcePath);
			image = readPngImage(input);
			pngSymbolImages.put(sourcePath, image);
			return image;
		} catch (IOException e) {
			logger.warn("unable to open symbol image: " + sourcePath, e);
		}
		return null;
	}

	private Map<String, BvgImage> bvgSymbolImages = new HashMap<>();

	private BvgImage getBvgSymbolImage(String sourcePath)
	{
		// logger.debug(sourcePath);
		BvgImage image = bvgSymbolImages.get(sourcePath);
		if (image != null) {
			return image;
		}
		try {
			InputStream input = styleDataProvider.getSymbol(sourcePath);
			image = readBvgImage(input);
			return image;
		} catch (IOException e) {
			logger.warn("unable to open symbol image: " + sourcePath, e);
		}
		return null;
	}

}