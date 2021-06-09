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

package de.topobyte.mapocado.android.rendering;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.Log;
import de.topobyte.adt.geo.BBox;
import de.topobyte.android.mapview.ReferenceCountedBitmap;
import de.topobyte.android.misc.utils.AndroidTimeUtil;
import de.topobyte.bvg.BvgAndroidPainter;
import de.topobyte.bvg.BvgIO;
import de.topobyte.bvg.BvgImage;
import de.topobyte.chromaticity.ColorCode;
import de.topobyte.jeography.core.Tile;
import de.topobyte.jeography.core.TileUtil;
import de.topobyte.jeography.tiles.source.ImageSource;
import de.topobyte.mapocado.android.mapfile.MapfileOpener;
import de.topobyte.mapocado.android.rendering.gather.CoordinateGatherer;
import de.topobyte.mapocado.android.rendering.gather.PathTextElement;
import de.topobyte.mapocado.android.rendering.gather.RelationGatherer;
import de.topobyte.mapocado.android.rendering.gather.WayGatherer;
import de.topobyte.mapocado.android.rendering.linesymbol.BvgLineSymbolRenderer;
import de.topobyte.mapocado.android.rendering.linesymbol.CanvasLineSymbolRenderer;
import de.topobyte.mapocado.android.rendering.linesymbol.PngLineSymbolRenderer;
import de.topobyte.mapocado.android.rendering.pathtext.AndroidPathLabeller;
import de.topobyte.mapocado.android.style.MapRenderConfig;
import de.topobyte.mapocado.android.style.RenderClassHelper;
import de.topobyte.mapocado.android.style.RenderElementGatherer;
import de.topobyte.mapocado.mapformat.Mapfile;
import de.topobyte.mapocado.mapformat.Mercator;
import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.disk.DiskTree;
import de.topobyte.mapocado.rendering.Clipping;
import de.topobyte.mapocado.rendering.RenderingLogic;
import de.topobyte.mapocado.rendering.pathtext.LinestringUtil;
import de.topobyte.mapocado.rendering.text.TextIntersectionChecker;
import de.topobyte.mapocado.rendering.text.TextIntersectionCheckerTree;
import de.topobyte.mapocado.styles.classes.ElementType;
import de.topobyte.mapocado.styles.classes.element.Area;
import de.topobyte.mapocado.styles.classes.element.BvgLineSymbol;
import de.topobyte.mapocado.styles.classes.element.BvgSymbol;
import de.topobyte.mapocado.styles.classes.element.Circle;
import de.topobyte.mapocado.styles.classes.element.Line;
import de.topobyte.mapocado.styles.classes.element.LineSymbol;
import de.topobyte.mapocado.styles.classes.element.PngLineSymbol;
import de.topobyte.mapocado.styles.classes.element.PngSymbol;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;
import de.topobyte.misc.util.TimeCounter;

public class MapocadoImageSource
		implements ImageSource<Tile, ReferenceCountedBitmap>
{

	private Mapfile mapfile = null;

	private MapRenderConfig mapRenderConfig = null;
	private int colorBackground = Color.WHITE;
	private RenderClassHelper renderClassHelper = null;
	private RenderElementGatherer renderGatherer = null;

	private int tileSize = Tile.SIZE;
	private float magnification = 1.0f;
	private float tileScaleFactor = 1.0f;
	private float combinedScaleFactor = 1.0f;

	public void setMapFile(MapfileOpener opener)
			throws IOException, ClassNotFoundException
	{
		mapfile = opener.open();
	}

	public void setRenderConfig(MapRenderConfig mapRenderConfig)
	{
		this.mapRenderConfig = mapRenderConfig;
		colorBackground = mapRenderConfig.getBackgroundColor();
		Metadata metadata = mapfile.getMetadata();
		renderClassHelper = new RenderClassHelper(
				mapRenderConfig.getObjectClasses(), metadata.getPoolForRefs());
		renderGatherer = new RenderElementGatherer(metadata.getPoolForRefs(),
				renderClassHelper);
		// clear image caches
		pngSymbols.clear();
		bvgSymbols.clear();
		textures.clear();
	}

	public void setTileScaleFactor(float tileScaleFactor)
	{
		this.tileScaleFactor = tileScaleFactor;
		tileSize = Math.round(tileScaleFactor * Tile.SIZE);
		calculateCombinedScaleFactor();
	}

	public float getTileScaleFactor()
	{
		return tileScaleFactor;
	}

	public void setMagnification(float magnification)
	{
		this.magnification = magnification;
		calculateCombinedScaleFactor();
	}

	public float getMagnification()
	{
		return magnification;
	}

	private void calculateCombinedScaleFactor()
	{
		combinedScaleFactor = magnification * tileScaleFactor;
	}

	private double QUERY_RATIO_WAY = 1.1;
	private double QUERY_RATIO_NODE = 1.5;
	private double ADD_WAY = QUERY_RATIO_WAY;
	private double SUB_WAY = QUERY_RATIO_WAY - 1;
	private double ADD_NODE = QUERY_RATIO_NODE;
	private double SUB_NODE = QUERY_RATIO_NODE - 1;

	private Clipping clipping = null;

	@Override
	public ReferenceCountedBitmap load(Tile tile)
	{
		Log.i("render", "load : " + mapfile);

		Bitmap bitmap = Bitmap.createBitmap(tileSize, tileSize,
				Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);

		Paint paint = new Paint();
		paint.setColor(colorBackground);
		canvas.drawRect(new Rect(0, 0, tileSize, tileSize), paint);

		int zoom = tile.getZoom();
		double extra = zoom < 20 ? 0 : zoom / (double) 20 * 0.2;
		BBox bboxRequestWay = TileUtil.getBoundingBox(
				tile.getTx() - SUB_WAY - extra, tile.getTx() + ADD_WAY + extra,
				tile.getTy() - SUB_WAY - extra, tile.getTy() + ADD_WAY + extra,
				tile.getZoom());
		BBox bboxRequestNode = TileUtil.getBoundingBox(tile.getTx() - SUB_NODE,
				tile.getTx() + ADD_NODE, tile.getTy() - SUB_NODE,
				tile.getTy() + ADD_NODE, tile.getZoom());
		// Log.i("render", String.format("tile: zoom: %d, x: %d, y: %d",
		// tile.getZoom(), tile.getTx(), tile.getTy()));
		// Log.i("render", "bbox: " + bbox);

		clipping = new Clipping(tileSize, tileSize, clippingFactor(zoom));

		IntervalTree<Integer, DiskTree<Node>> treeNodes = mapfile
				.getTreeNodes();
		IntervalTree<Integer, DiskTree<Way>> treeWays = mapfile.getTreeWays();
		IntervalTree<Integer, DiskTree<Relation>> treeRelations = mapfile
				.getTreeRelations();

		BoundingBox rectRequestWay = new BoundingBox(bboxRequestWay.getLon1(),
				bboxRequestWay.getLon2(), bboxRequestWay.getLat1(),
				bboxRequestWay.getLat2(), true);

		BoundingBox rectRequestNode = new BoundingBox(bboxRequestNode.getLon1(),
				bboxRequestNode.getLon2(), bboxRequestNode.getLat1(),
				bboxRequestNode.getLat2(), true);

		/*
		 * query and gather data
		 */

		CoordinateGatherer nodeGatherer = new CoordinateGatherer(tile.getZoom(),
				renderClassHelper, renderGatherer);
		WayGatherer wayGatherer = new WayGatherer(tile.getZoom(),
				renderClassHelper, renderGatherer);
		RelationGatherer relationGatherer = new RelationGatherer(tile.getZoom(),
				renderClassHelper, renderGatherer);

		renderGatherer.clear();

		long startQuery = System.currentTimeMillis();
		try {
			AndroidTimeUtil.time("node query");
			List<DiskTree<Node>> nodeTrees = treeNodes
					.getObjects(tile.getZoom());
			for (DiskTree<Node> tree : nodeTrees) {
				tree.intersectionQuery(rectRequestNode, nodeGatherer);
			}
			AndroidTimeUtil.time("node query", "query",
					"time for node query: %d");

			AndroidTimeUtil.time("way query");
			List<DiskTree<Way>> wayTrees = treeWays.getObjects(tile.getZoom());
			for (DiskTree<Way> tree : wayTrees) {
				tree.intersectionQuery(rectRequestWay, wayGatherer);
			}
			AndroidTimeUtil.time("way query", "query",
					"time for way query: %d");

			AndroidTimeUtil.time("relation query");
			List<DiskTree<Relation>> relationTrees = treeRelations
					.getObjects(tile.getZoom());
			for (DiskTree<Relation> tree : relationTrees) {
				tree.intersectionQuery(rectRequestWay, relationGatherer);
			}
			AndroidTimeUtil.time("relation query", "query",
					"time for relation query: %d");
		} catch (IOException e) {
			e.printStackTrace();
		}
		long endQuery = System.currentTimeMillis();
		long timeQuery = endQuery - startQuery;
		Log.i("render", "time for queries: " + timeQuery);

		// render objects
		render(canvas, tile, tile.getZoom());

		return new ReferenceCountedBitmap(bitmap);
	}

	/*
	 * Rendering
	 */

	private void render(Canvas canvas, Tile tile, int zoom)
	{
		Mercator mercator = new Mercator(tile, tileSize);

		int countLines = 0, countAreas = 0, countNodes = 0;

		TimeCounter tc = new TimeCounter(3);

		AndroidTimeUtil.time("r1");
		for (int i = 0; i < renderClassHelper.renderElements.length; i++) {
			int k = renderClassHelper.zOrderedRenderElementIds[i];
			RenderElement renderElement = renderClassHelper.renderElements[k];
			ElementType elementType = renderClassHelper.renderElementTypes[k];
			List<Coordinate> objectsNodes = renderGatherer.bucketsCoordinates[k];
			List<Linestring> objectsWays = renderGatherer.bucketsWays[k];
			List<Multipolygon> objectsRelations = renderGatherer.bucketsRelations[k];

			switch (elementType) {
			case LINE: {
				tc.start(0);
				Line line = (Line) renderElement;
				renderLine(canvas, mercator, zoom, line, objectsWays,
						objectsRelations);
				countLines += objectsWays.size() + objectsRelations.size();
				tc.stop(0);
				break;
			}
			case AREA: {
				tc.start(1);
				Area area = (Area) renderElement;
				renderArea(canvas, mercator, zoom, area, objectsWays,
						objectsRelations);
				countAreas += objectsWays.size() + objectsRelations.size();
				tc.stop(1);
				break;
			}
			case CIRCLE: {
				tc.start(2);
				Circle circle = (Circle) renderElement;
				renderCircle(canvas, mercator, zoom, circle, objectsNodes);
				countAreas += objectsNodes.size();
				tc.stop(2);
				break;
			}
			case PNG_LINESYMBOL: {
				PngLineSymbol lineSymbol = (PngLineSymbol) renderElement;
				renderLineSymbol(canvas, mercator, zoom, lineSymbol,
						objectsWays);
				break;
			}
			case BVG_LINESYMBOL: {
				BvgLineSymbol lineSymbol = (BvgLineSymbol) renderElement;
				renderLineSymbol(canvas, mercator, zoom, lineSymbol,
						objectsWays);
				break;
			}
			default:
				// Log.i("render", rule.getClass() + ": " + rule);
				break;
			}
		}
		AndroidTimeUtil.time("r1", "render", "time for line/area elements: %d");
		Log.i("render", "number of lines: " + countLines);
		Log.i("render", "number of areas: " + countAreas);
		Log.i("render", "number of nodes: " + countNodes);
		Log.i("render", "time for lines: " + tc.getTotal(0));
		Log.i("render", "time for areas: " + tc.getTotal(1));
		Log.i("render", "time for circles: " + tc.getTotal(2));

		AndroidTimeUtil.time("r2");
		for (int i = 0; i < renderClassHelper.pngSymbolElementIds.length; i++) {
			int k = renderClassHelper.pngSymbolElementIds[i];
			RenderElement renderElement = renderClassHelper.renderElements[k];
			PngSymbol symbol = (PngSymbol) renderElement;
			List<Coordinate> objects = renderGatherer.bucketsCoordinates[k];
			renderSymbol(canvas, mercator, symbol, objects);
		}
		AndroidTimeUtil.time("r2", "render",
				"time for png symbols elements: %d");

		AndroidTimeUtil.time("r3");
		for (int i = 0; i < renderClassHelper.bvgSymbolElementIds.length; i++) {
			int k = renderClassHelper.bvgSymbolElementIds[i];
			RenderElement renderElement = renderClassHelper.renderElements[k];
			BvgSymbol symbol = (BvgSymbol) renderElement;
			List<Coordinate> objects = renderGatherer.bucketsCoordinates[k];
			renderSymbol(canvas, mercator, symbol, objects);
		}
		AndroidTimeUtil.time("r3", "render",
				"time for bvg symbols elements: %d");

		AndroidTimeUtil.time("r4");
		Clipping hitTest = LinestringUtil.createClipping(tile, 1.05f);
		TextIntersectionChecker checker = new TextIntersectionCheckerTree();
		AndroidPathLabeller labeller = new AndroidPathLabeller(canvas, checker,
				hitTest, zoom, mercator, combinedScaleFactor);
		for (int i = 0; i < renderClassHelper.textPathElementIds.length; i++) {
			int k = renderClassHelper.textPathElementIds[i];
			List<PathTextElement> objects = renderGatherer.bucketsPathTexts[k];
			if (objects.size() == 0) {
				continue;
			}
			RenderElement renderElement = renderClassHelper.renderElements[k];
			PathTextSlim pathText = (PathTextSlim) renderElement;
			labeller.setStyle(pathText);

			for (PathTextElement element : objects) {
				// fetch data
				String labelText = element.text;
				if (labelText == null) {
					continue;
				}
				Linestring string = element.string;

				labeller.renderPathText(string, labelText);
			}
		}
		AndroidTimeUtil.time("r4", "render", "time for path text elements: %d");
	}

	private void renderLine(Canvas canvas, Mercator mercator, int zoom,
			Line line, List<Linestring> objectsWays,
			List<Multipolygon> objectsRelations)
	{
		if (objectsWays.size() == 0 && objectsRelations.size() == 0) {
			return;
		}

		// setup style
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Paint.Style.STROKE);

		Cap lineCap = StyleConversion.getLineCap(line.getCapType());
		float strokeWidth = line.getStrokeWidth();
		strokeWidth = RenderingLogic.scaleStroke(strokeWidth, zoom);
		strokeWidth = RenderingLogic.scale(strokeWidth, tileScaleFactor);
		List<Float> dashArray = line.getDashArray();
		if (dashArray == null) {
			paint.setStrokeWidth(strokeWidth);
			paint.setStrokeCap(lineCap);
			paint.setPathEffect(null);
		} else {
			float[] dash = new float[dashArray.size()];
			for (int i = 0; i < dashArray.size(); i++) {
				dash[i] = RenderingLogic.scaleDash(dashArray.get(i), zoom);
			}
			paint.setStrokeWidth(strokeWidth);
			paint.setStrokeCap(lineCap);
			DashPathEffect dashEffect = new DashPathEffect(dash, 0);
			paint.setPathEffect(dashEffect);
		}
		paint.setColor(StyleConversion.getColor(line.getStroke()));

		Path path = new Path();

		// iterate ways
		for (int i = 0; i < objectsWays.size(); i++) {
			path.rewind();
			Linestring string = objectsWays.get(i);

			GeometryConversion.createClippedPath(path, string, mercator,
					clipping);
			canvas.drawPath(path, paint);
		}

		// iterate relations
		for (int i = 0; i < objectsRelations.size(); i++) {
			path.rewind();
			Multipolygon polygon = objectsRelations.get(i);
			GeometryConversion.createPath(path, polygon, mercator);
			canvas.drawPath(path, paint);
		}
	}

	private static int clippingFactor(int zoom)
	{
		if (zoom <= 12) {
			return zoom;
		} else if (zoom <= 16) {
			return zoom * 2;
		} else if (zoom <= 20) {
			return zoom * 3;
		} else {
			return zoom * 4;
		}
	}

	private void renderArea(Canvas canvas, Mercator mercator, int zoom,
			Area area, List<Linestring> objectsWays,
			List<Multipolygon> objectsRelations)
	{
		if (objectsWays.size() == 0 && objectsRelations.size() == 0) {
			return;
		}

		// create pathsrenderPathText
		int numElements = objectsWays.size() + objectsRelations.size();
		List<Path> paths = new ArrayList<>(numElements);

		// iterate ways
		for (int i = 0; i < objectsWays.size(); i++) {
			Linestring way = objectsWays.get(i);
			if (!way.isClosed()) {
				continue;
			}
			Path path = GeometryConversion.createPath(way, false, mercator);
			paths.add(path);
		}

		// iterate relations
		for (int i = 0; i < objectsRelations.size(); i++) {
			Multipolygon polygon = objectsRelations.get(i);
			Path path = GeometryConversion.createPath(polygon, mercator);
			paths.add(path);
		}

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		ColorCode fillColor = area.getFill();
		String fillSource = area.getSource();
		if (fillColor != null || fillSource != null) {
			paint.setStyle(Paint.Style.FILL);

			if (fillColor != null) {
				paint.setColor(StyleConversion.getColor(fillColor));
			}

			if (area.getSource() != null) {
				paint = new Paint(Paint.FILTER_BITMAP_FLAG);
				Bitmap texture = getTexture(fillSource);
				if (texture != null) {
					BitmapShader shader = new BitmapShader(texture,
							Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
					paint.setShader(shader);
				}
			}

			for (Path path : paths) {
				path.setFillType(FillType.EVEN_ODD);
				canvas.drawPath(path, paint);
			}
		}

		ColorCode strokeColor = area.getStroke();
		if (strokeColor != null) {
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(
					RenderingLogic.scaleStroke(area.getStrokeWidth(), zoom));
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setPathEffect(null);
			paint.setColor(StyleConversion.getColor(strokeColor));
			for (Path path : paths) {
				canvas.drawPath(path, paint);
			}
		}
	}

	private void renderCircle(Canvas canvas, Mercator mercator, int zoom,
			Circle circle, List<Coordinate> objects)
	{
		if (objects.size() == 0) {
			return;
		}

		// styles
		Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFill.setStyle(Paint.Style.FILL);
		paintFill.setColor(StyleConversion.getColor(circle.getFill()));

		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(StyleConversion.getColor(circle.getStroke()));

		// sizes
		float strokeWidth = circle.getStrokeWidth();
		float radius = circle.getRadius();
		if (circle.isScaleRadius()) {
			strokeWidth = RenderingLogic.scaleStroke(strokeWidth, zoom);
			radius = RenderingLogic.scaleRadius(radius, zoom);
		}

		radius = RenderingLogic.scale(radius, tileScaleFactor);
		strokeWidth = RenderingLogic.scale(strokeWidth, tileScaleFactor);

		// iterate objects
		for (Coordinate coordinate : objects) {
			int mx = coordinate.getX();
			int my = coordinate.getY();
			float x = mercator.getX(mx);
			float y = mercator.getY(my);

			canvas.drawCircle(x, y, radius, paintFill);
			canvas.drawCircle(x, y, radius, paintStroke);
		}
	}

	private void renderSymbol(Canvas canvas, Mercator mercator,
			PngSymbol symbol, List<Coordinate> objects)
	{
		String source = symbol.getSource();
		Bitmap bitmap = getPngSymbol(source);

		if (bitmap == null) {
			return;
		}

		float offX = -bitmap.getWidth() / 2f;
		float offY = -bitmap.getHeight() / 2f;

		Paint paint = new Paint();
		for (Coordinate coordinate : objects) {
			int mx = coordinate.getX();
			int my = coordinate.getY();
			float x = mercator.getX(mx);
			float y = mercator.getY(my);

			canvas.drawBitmap(bitmap, x + offX, y + offY, paint);
		}
	}

	private void renderLineSymbol(Canvas canvas, Mercator mercator, int zoom,
			BvgLineSymbol symbol, List<Linestring> objectsWays)
	{
		String source = symbol.getSource();
		BvgImage image;
		try {
			image = getBvgSymbol(source);
		} catch (IOException e) {
			Log.w("render", "unable to load icon: " + source);
			return;
		}
		if (image == null) {
			Log.w("render", "unable to load icon: " + source);
			return;
		}

		CanvasLineSymbolRenderer<?> lineSymbolRenderer = new BvgLineSymbolRenderer(
				image);

		float width = symbol.getWidth();
		float height = (float) (image.getHeight() / image.getWidth() * width);

		renderLineSymbol(canvas, mercator, zoom, lineSymbolRenderer, symbol,
				height, width, objectsWays);
	}

	private void renderLineSymbol(Canvas canvas, Mercator mercator, int zoom,
			PngLineSymbol symbol, List<Linestring> objectsWays)
	{
		String source = symbol.getSource();
		Bitmap image = getPngSymbol(source);

		if (image == null) {
			Log.w("render", "unable to load icon: " + source);
			return;
		}

		CanvasLineSymbolRenderer<?> lineSymbolRenderer = new PngLineSymbolRenderer(
				image);

		int width = image.getWidth();
		int height = image.getHeight();

		renderLineSymbol(canvas, mercator, zoom, lineSymbolRenderer, symbol,
				height, width, objectsWays);
	}

	private void renderLineSymbol(Canvas canvas, Mercator mercator, int zoom,
			CanvasLineSymbolRenderer<?> lineSymbolRenderer, LineSymbol symbol,
			float height, float width, List<Linestring> objectsWays)
	{
		boolean repeat = symbol.isRepeat();
		float repeatDistance = symbol.getRepeatDistance();
		float offset = symbol.getOffset();

		double symbolWidthStorage = mercator.getLengthStorageUnits(width, zoom);
		double offsetStorage = mercator.getLengthStorageUnits(offset, zoom);
		double repeatStorage = mercator.getLengthStorageUnits(repeatDistance,
				zoom);

		lineSymbolRenderer.init(canvas, mercator, height, symbolWidthStorage,
				offsetStorage, tileScaleFactor);

		if (!repeat) {
			for (Linestring string : objectsWays) {
				lineSymbolRenderer.renderLineSymbol(string);
			}
		} else {
			for (Linestring string : objectsWays) {
				lineSymbolRenderer.renderLineSymbol(string, repeatStorage);
			}
		}
	}

	// cache for texture bitmaps
	private Map<String, Bitmap> textures = new HashMap<>();

	private Bitmap getTexture(String patternName)
	{
		Bitmap bitmap = textures.get(patternName);
		if (bitmap == null) {
			File file = mapRenderConfig.getTexture(patternName);
			Log.i("render", "file: " + file);
			Log.i("render", "exists? " + file.exists());
			bitmap = BitmapFactory.decodeFile(file.getPath());
			Log.i("render", "bitmap: " + bitmap);
			textures.put(patternName, bitmap);
		}
		return bitmap;
	}

	// cache for symbol bitmaps
	private Map<String, Bitmap> pngSymbols = new HashMap<>();

	private Bitmap getPngSymbol(String source)
	{
		Bitmap bitmap = pngSymbols.get(source);
		if (bitmap == null) {
			File file = mapRenderConfig.getSymbol(source);
			Log.i("render", "file: " + file);
			Log.i("render", "exists? " + file.exists());
			bitmap = BitmapFactory.decodeFile(file.getPath());
			Log.i("render", "bitmap: " + bitmap);
			pngSymbols.put(source, bitmap);
		}
		return bitmap;
	}

	private void renderSymbol(Canvas canvas, Mercator mercator,
			BvgSymbol symbol, List<Coordinate> objects)
	{
		String source = symbol.getSource();
		BvgImage image;
		try {
			image = getBvgSymbol(source);
		} catch (IOException e) {
			Log.w("render", "unable to load icon: " + source);
			return;
		}

		if (image == null) {
			Log.w("render", "unable to load icon: " + source);
			return;
		}

		float height = symbol.getHeight();
		float width = (float) (image.getWidth() / image.getHeight()
				* symbol.getHeight());

		height = RenderingLogic.scale(height, tileScaleFactor);
		width = RenderingLogic.scale(width, tileScaleFactor);

		float scale = (float) (height / image.getHeight());

		float offX = -width / 2f;
		float offY = -height / 2f;

		for (Coordinate coordinate : objects) {
			int mx = coordinate.getX();
			int my = coordinate.getY();
			float x = mercator.getX(mx);
			float y = mercator.getY(my);

			BvgAndroidPainter.draw(canvas, image, x + offX, y + offY, scale,
					scale, scale);
		}
	}

	// cache for symbol bitmaps
	private Map<String, BvgImage> bvgSymbols = new HashMap<>();

	private BvgImage getBvgSymbol(String source) throws IOException
	{
		BvgImage image = bvgSymbols.get(source);
		if (image == null) {
			File file = mapRenderConfig.getSymbol(source);
			Log.i("render", "file: " + file);
			Log.i("render", "exists? " + file.exists());
			image = BvgIO.read(file);
			Log.i("render", "image: " + image);
			bvgSymbols.put(source, image);
		}
		return image;
	}

}
