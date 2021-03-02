package de.topobyte.mapocado.styles.classes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.classes.element.Area;
import de.topobyte.mapocado.styles.classes.element.BvgLineSymbol;
import de.topobyte.mapocado.styles.classes.element.BvgSymbol;
import de.topobyte.mapocado.styles.classes.element.Caption;
import de.topobyte.mapocado.styles.classes.element.Circle;
import de.topobyte.mapocado.styles.classes.element.Line;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.PathText;
import de.topobyte.mapocado.styles.classes.element.PngLineSymbol;
import de.topobyte.mapocado.styles.classes.element.PngSymbol;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.misc.ColorParser;
import de.topobyte.mapocado.styles.misc.EnumParser;
import de.topobyte.mapocado.styles.misc.PrimitiveParser;
import de.topobyte.mapocado.styles.misc.enums.CapType;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class ClassFileHandler extends DefaultHandler
{

	// xml strings

	private static String ELEMENT_CLASSES = "classes";
	private static String ELEMENT_GROUP = "classgroup";
	private static String ELEMENT_CLASS = "class";
	private static String ELEMENT_CAPTION = "caption";
	private static String ELEMENT_AREA = "area";
	private static String ELEMENT_SYMBOL = "symbol";
	private static String ELEMENT_CIRCLE = "circle";
	private static String ELEMENT_LINE = "line";
	private static String ELEMENT_PATHTEXT = "pathText";
	private static String ELEMENT_LINESYMBOL = "lineSymbol";

	private static String ATTRIBUTE_MAP_BG = "map-background";
	private static String ATTRIBUTE_MAP_OVERLAY_INNER = "map-overlay-inner";
	private static String ATTRIBUTE_MAP_OVERLAY_OUTER = "map-overlay-outer";
	private static String ATTRIBUTE_MAP_OVERLAY_GPS_INNER = "map-overlay-gps-inner";
	private static String ATTRIBUTE_MAP_OVERLAY_GPS_OUTER = "map-overlay-gps-outer";

	// data objects

	private ColorCode background = DEFAULT_BACKGROUND;
	private ColorCode overlayInner = DEFAULT_OVERLAY_INNER;
	private ColorCode overlayOuter = DEFAULT_OVERLAY_OUTER;
	private ColorCode overlayGpsInner = DEFAULT_OVERLAY_GPS_INNER;
	private ColorCode overlayGpsOuter = DEFAULT_OVERLAY_GPS_OUTER;
	private List<ObjectClass> classes = new ArrayList<>();
	private List<ObjectClass> groupClasses = new ArrayList<>();

	// intermediate parsing fields

	private int xmlLevel = 0; // depth within the xml document tree
	private int level = 1; // z-order, i.e. order of render element appearance

	private String currentId = null;
	private List<RenderElement> currentElements = null;
	private int currentMinZoom = 0;
	private int currentMaxZoom = 0;

	// data getters

	public ColorCode getBackground()
	{
		return background;
	}

	public ColorCode getOverlayInner()
	{
		return overlayInner;
	}

	public ColorCode getOverlayOuter()
	{
		return overlayOuter;
	}

	public ColorCode getOverlayGpsInner()
	{
		return overlayGpsInner;
	}

	public ColorCode getOverlayGpsOuter()
	{
		return overlayGpsOuter;
	}

	public List<ObjectClass> getObjectClasses()
	{
		return classes;
	}

	// xml parsing

	@Override
	public void startDocument() throws SAXException
	{

	}

	@Override
	public void endDocument() throws SAXException
	{

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		xmlLevel--;
		if (localName.equals(ELEMENT_CLASS)) {
			RenderElement[] elements = currentElements
					.toArray(new RenderElement[0]);
			ObjectClass objectClass = new ObjectClass(currentId, elements,
					currentMinZoom, currentMaxZoom);
			if (xmlLevel == 1) {
				classes.add(objectClass);
			} else if (xmlLevel == 2) {
				groupClasses.add(objectClass);
			}
		} else if (localName.equals(ELEMENT_GROUP)) {
			RenderElement[] elements = currentElements
					.toArray(new RenderElement[0]);
			for (ObjectClass objectClass : groupClasses) {
				classes.add(objectClass);
				objectClass.elements = elements;
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		xmlLevel++;
		if (xmlLevel == 1) {
			if (localName.equals(ELEMENT_CLASSES)) {
				parseClasses(attributes);
			}
		} else {
			// level > 1
			if (localName.equals(ELEMENT_CLASS)) {
				parseClass(attributes);
			} else if (localName.equals(ELEMENT_GROUP)) {
				groupClasses = new ArrayList<>();
			} else if (localName.equals(ELEMENT_LINE)) {
				parseLine(attributes);
			} else if (localName.equals(ELEMENT_AREA)) {
				parseArea(attributes);
			} else if (localName.equals(ELEMENT_CIRCLE)) {
				parseCircle(attributes);
			} else if (localName.equals(ELEMENT_SYMBOL)) {
				parseSymbol(attributes);
			} else if (localName.equals(ELEMENT_CAPTION)) {
				parseCaption(attributes);
			} else if (localName.equals(ELEMENT_PATHTEXT)) {
				parsePathText(attributes);
			} else if (localName.equals(ELEMENT_LINESYMBOL)) {
				parseLineSymbol(attributes);
			}
		}
	}

	private static int DEFAULT_ZOOM_MIN = 0;
	private static int DEFAULT_ZOOM_MAX = 127;

	private static ColorCode DEFAULT_BACKGROUND = new ColorCode(0xFFFFFF,
			false);
	private static ColorCode DEFAULT_OVERLAY_INNER = new ColorCode(0x000000,
			false);
	private static ColorCode DEFAULT_OVERLAY_OUTER = new ColorCode(0xFFFFFF,
			false);
	private static ColorCode DEFAULT_OVERLAY_GPS_INNER = new ColorCode(
			0x60ff0000, true);
	private static ColorCode DEFAULT_OVERLAY_GPS_OUTER = new ColorCode(
			0xffff0000, true);

	private static CapType DEFAULT_CAP_TYPE = CapType.ROUND;
	private static ColorCode DEFAULT_AREA_FILL = null;
	private static ColorCode DEFAULT_AREA_STROKE = null;
	private static ColorCode DEFAULT_LINE_STROKE = new ColorCode(0x00000000,
			true);
	private static float DEFAULT_STROKE_WIDTH = 0;

	private static float DEFAULT_PATHTEXT_FONT_SIZE = 0;
	private static float DEFAULT_PATHTEXT_STROKE_WIDTH = 0;
	private static ColorCode DEFAULT_PATHTEXT_STROKE = new ColorCode(0x000000,
			false);
	private static ColorCode DEFAULT_PATHTEXT_FILL = new ColorCode(0x000000,
			false);
	private static FontFamily DEFAULT_PATHTEXT_FONT_FAMILY = FontFamily.SANS_SERIF;
	private static FontStyle DEFAULT_PATHTEXT_FONT_STYLE = FontStyle.NORMAL;

	private static float DEFAULT_CAPTION_DY = 0;
	private static float DEFAULT_CAPTION_FONT_SIZE = 0;
	private static float DEFAULT_CAPTION_STROKE_WIDTH = 0;
	private static ColorCode DEFAULT_CAPTION_STROKE = new ColorCode(0x000000,
			false);
	private static ColorCode DEFAULT_CAPTION_FILL = new ColorCode(0x000000,
			false);
	private static FontFamily DEFAULT_CAPTION_FONT_FAMILY = FontFamily.SANS_SERIF;
	private static FontStyle DEFAULT_CAPTION_FONT_STYLE = FontStyle.NORMAL;

	private static boolean DEFAULT_CIRCLE_SCALE_RADIUS = false;
	private static ColorCode DEFAULT_CIRCLE_FILL = new ColorCode(0x00000000,
			true);
	private static ColorCode DEFAULT_CIRCLE_STROKE = new ColorCode(0x00000000,
			true);
	private static float DEFAULT_CIRCLE_STROKE_WIDTH = 0.0f;

	private void parseClasses(Attributes attributes)
	{
		String valBackground = attributes.getValue(ATTRIBUTE_MAP_BG);
		if (valBackground != null) {
			background = ColorParser.parseColor(valBackground,
					DEFAULT_BACKGROUND);
		}
		String valOverlayInner = attributes
				.getValue(ATTRIBUTE_MAP_OVERLAY_INNER);
		if (valBackground != null) {
			overlayInner = ColorParser.parseColor(valOverlayInner,
					DEFAULT_OVERLAY_INNER);
		}
		String valOverlayOuter = attributes
				.getValue(ATTRIBUTE_MAP_OVERLAY_OUTER);
		if (valBackground != null) {
			overlayOuter = ColorParser.parseColor(valOverlayOuter,
					DEFAULT_OVERLAY_OUTER);
		}
		String valOverlayGpsInner = attributes
				.getValue(ATTRIBUTE_MAP_OVERLAY_GPS_INNER);
		if (valBackground != null) {
			overlayGpsInner = ColorParser.parseColor(valOverlayGpsInner,
					DEFAULT_OVERLAY_GPS_INNER);
		}
		String valOverlayGpsOuter = attributes
				.getValue(ATTRIBUTE_MAP_OVERLAY_GPS_OUTER);
		if (valBackground != null) {
			overlayGpsOuter = ColorParser.parseColor(valOverlayGpsOuter,
					DEFAULT_OVERLAY_GPS_OUTER);
		}
	}

	private static Pattern patternDashArray = Pattern.compile(
			"([0-9]+(\\.[0-9]+)? *, *[0-9]+(\\.[0-9]+)? *, *)*[0-9]+(\\.[0-9]+)? *, *[0-9]+(\\.[0-9]+)?");

	private static Pattern patternFloat = Pattern
			.compile("([0-9]+(\\.[0-9]+)?)");

	private List<Float> parseDashArray(String val)
	{
		if (val == null) {
			return null;
		}
		Matcher matcher = patternDashArray.matcher(val);
		if (!matcher.matches()) {
			return null;
		}
		int pos = 0;
		List<Float> dashArray = new ArrayList<>();
		while (true) {
			matcher = patternFloat.matcher(val);
			boolean found = matcher.find(pos);
			if (!found) {
				break;
			}
			String value = matcher.group(1);
			float v = Float.parseFloat(value);
			dashArray.add(v);
			pos = matcher.end();
		}
		return dashArray;
	}

	private static String parseSource(String value)
	{
		if (value == null) {
			return null;
		}
		return value;
	}

	private void parseClass(Attributes attributes)
	{
		currentElements = new ArrayList<>();
		currentId = attributes.getValue("id");
		currentMinZoom = PrimitiveParser
				.parseInt(attributes.getValue("zoom-min"), DEFAULT_ZOOM_MIN);
		currentMaxZoom = PrimitiveParser
				.parseInt(attributes.getValue("zoom-max"), DEFAULT_ZOOM_MAX);
	}

	private void parseLine(Attributes attributes)
	{
		String valStroke = attributes.getValue("stroke");
		String valStrokeWidth = attributes.getValue("stroke-width");
		String valStrokeDashArray = attributes.getValue("stroke-dasharray");
		String valStrokeLineCap = attributes.getValue("stroke-linecap");

		ColorCode stroke = ColorParser.parseColor(valStroke,
				DEFAULT_LINE_STROKE);
		float strokeWidth = PrimitiveParser.parseFloat(valStrokeWidth,
				DEFAULT_STROKE_WIDTH);
		CapType capType = EnumParser.parseCapType(valStrokeLineCap,
				DEFAULT_CAP_TYPE);
		List<Float> dashArray = parseDashArray(valStrokeDashArray);

		Line line = new Line(level++, stroke, strokeWidth, capType, dashArray);
		currentElements.add(line);
	}

	private void parseLineSymbol(Attributes attributes)
	{
		String src = attributes.getValue("src");
		String valRepeat = attributes.getValue("repeat");
		String valRepeatDistance = attributes.getValue("repeat-distance");
		String valOffset = attributes.getValue("offset");

		boolean repeat = PrimitiveParser.parseBoolean(valRepeat, false);
		float repeatDistance = 100;
		if (repeat) {
			repeatDistance = PrimitiveParser.parseFloat(valRepeatDistance,
					repeatDistance);
		}
		float offset = PrimitiveParser.parseFloat(valOffset, 0);

		if (src.endsWith(".bvg")) {
			String valWidth = attributes.getValue("width");
			float width = PrimitiveParser.parseFloat(valWidth, 20);
			BvgLineSymbol symbol = new BvgLineSymbol(level++, src, width,
					offset, repeat, repeatDistance);
			currentElements.add(symbol);
		} else if (src.endsWith(".png")) {
			PngLineSymbol symbol = new PngLineSymbol(level++, src, offset,
					repeat, repeatDistance);
			currentElements.add(symbol);
		}
	}

	private void parseArea(Attributes attributes)
	{
		String valSrc = attributes.getValue("src");
		String valFill = attributes.getValue("fill");
		String valStroke = attributes.getValue("stroke");
		String valStrokeWidth = attributes.getValue("stroke-width");

		String source = parseSource(valSrc);
		ColorCode fill = ColorParser.parseColor(valFill, DEFAULT_AREA_FILL);
		ColorCode stroke = ColorParser.parseColor(valStroke,
				DEFAULT_AREA_STROKE);
		float strokeWidth = PrimitiveParser.parseFloat(valStrokeWidth,
				DEFAULT_STROKE_WIDTH);

		Area area = new Area(level++, source, fill, stroke, strokeWidth);
		currentElements.add(area);
	}

	private void parseCircle(Attributes attributes)
	{
		String valRadius = attributes.getValue("r"); // required
		String valScaleRadius = attributes.getValue("scale-radius"); // optional
		String valFill = attributes.getValue("fill"); // optional
		String valStroke = attributes.getValue("stroke"); // optional
		String valStrokeWidth = attributes.getValue("stroke-width"); // optional

		float radius = PrimitiveParser.parseFloat(valRadius, 0.0f);
		boolean scaleRadius = PrimitiveParser.parseBoolean(valScaleRadius,
				DEFAULT_CIRCLE_SCALE_RADIUS);
		ColorCode fill = ColorParser.parseColor(valFill, DEFAULT_CIRCLE_FILL);
		ColorCode stroke = ColorParser.parseColor(valStroke,
				DEFAULT_CIRCLE_STROKE);
		float strokeWidth = PrimitiveParser.parseFloat(valStrokeWidth,
				DEFAULT_CIRCLE_STROKE_WIDTH);

		Circle circle = new Circle(level++, radius, scaleRadius, fill, stroke,
				strokeWidth);
		currentElements.add(circle);
	}

	private void parsePathText(Attributes attributes)
	{
		String valK = attributes.getValue("k");
		String valFontFamily = attributes.getValue("font-family");
		String valFontStyle = attributes.getValue("font-style");
		String valFontSize = attributes.getValue("font-size");
		String valFill = attributes.getValue("fill");
		String valStroke = attributes.getValue("stroke");
		String valStrokeWidth = attributes.getValue("stroke-width");

		float fontSize = PrimitiveParser.parseFloat(valFontSize,
				DEFAULT_PATHTEXT_FONT_SIZE);
		float strokeWidth = PrimitiveParser.parseFloat(valStrokeWidth,
				DEFAULT_PATHTEXT_STROKE_WIDTH);
		ColorCode stroke = ColorParser.parseColor(valStroke,
				DEFAULT_PATHTEXT_STROKE);
		ColorCode fill = ColorParser.parseColor(valFill, DEFAULT_PATHTEXT_FILL);
		FontFamily fontFamily = EnumParser.parseFontFamiliy(valFontFamily,
				DEFAULT_PATHTEXT_FONT_FAMILY);
		FontStyle fontStyle = EnumParser.parseFontStyle(valFontStyle,
				DEFAULT_PATHTEXT_FONT_STYLE);

		PathText pathText = new PathText(level++, valK, fontSize, strokeWidth,
				fill, stroke, fontFamily, fontStyle);
		currentElements.add(pathText);
	}

	private void parseSymbol(Attributes attributes)
	{
		String src = attributes.getValue("src");
		if (src.endsWith(".bvg")) {
			String sHeight = attributes.getValue("height");
			float height = PrimitiveParser.parseFloat(sHeight, 20);
			BvgSymbol symbol = new BvgSymbol(level++, src, height);
			currentElements.add(symbol);
		} else if (src.endsWith(".png")) {
			PngSymbol symbol = new PngSymbol(level++, src);
			currentElements.add(symbol);
		}
	}

	private void parseCaption(Attributes attributes)
	{
		String valK = attributes.getValue("k");
		String valDy = attributes.getValue("dy");
		String valFontFamily = attributes.getValue("font-family");
		String valFontStyle = attributes.getValue("font-style");
		String valFontSize = attributes.getValue("font-size");
		String valFill = attributes.getValue("fill");
		String valStroke = attributes.getValue("stroke");
		String valStrokeWidth = attributes.getValue("stroke-width");
		String valTag = attributes.getValue("tag");

		float fontSize = PrimitiveParser.parseFloat(valFontSize,
				DEFAULT_CAPTION_FONT_SIZE);
		float strokeWidth = PrimitiveParser.parseFloat(valStrokeWidth,
				DEFAULT_CAPTION_STROKE_WIDTH);
		boolean hasDeltaY = valDy != null;
		float dy = PrimitiveParser.parseFloat(valDy, DEFAULT_CAPTION_DY);
		ColorCode stroke = ColorParser.parseColor(valStroke,
				DEFAULT_CAPTION_STROKE);
		ColorCode fill = ColorParser.parseColor(valFill, DEFAULT_CAPTION_FILL);
		FontFamily fontFamily = EnumParser.parseFontFamiliy(valFontFamily,
				DEFAULT_CAPTION_FONT_FAMILY);
		FontStyle fontStyle = EnumParser.parseFontStyle(valFontStyle,
				DEFAULT_CAPTION_FONT_STYLE);

		Caption caption = new Caption(level++, valTag, valK, hasDeltaY, dy,
				fontSize, strokeWidth, fill, stroke, fontFamily, fontStyle);

		currentElements.add(caption);
	}
}
