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

package de.topobyte.mapocado.styles.labels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.labels.elements.DotLabel;
import de.topobyte.mapocado.styles.labels.elements.IconLabel;
import de.topobyte.mapocado.styles.labels.elements.Label;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.LabelType;
import de.topobyte.mapocado.styles.labels.elements.PlainLabel;
import de.topobyte.mapocado.styles.labels.elements.Rule;
import de.topobyte.mapocado.styles.misc.ColorParser;
import de.topobyte.mapocado.styles.misc.EnumParser;
import de.topobyte.mapocado.styles.misc.PrimitiveParser;
import de.topobyte.mapocado.styles.misc.enums.FontFamily;
import de.topobyte.mapocado.styles.misc.enums.FontStyle;

public class LabelFileHandler extends DefaultHandler
{

	final static Logger logger = LoggerFactory
			.getLogger(LabelFileHandler.class);

	// xml strings

	// private static String ELEMENT_LABELCONFIG = "labelconfig";
	// private static String ELEMENT_COLORS = "colors";
	private static String ELEMENT_COLOR = "color";
	// private static String ELEMENT_LABELS = "labels";
	private static String ELEMENT_LABEL = "label";
	private static String ELEMENT_RULE = "rule";

	// data objects

	private List<Label> labels = new ArrayList<>();
	private List<Rule> rules = new ArrayList<>();
	private Map<Rule, LabelContainer> ruleToLabel = new HashMap<>();

	// intermediate parsing fields

	private int xmlLevel = 0; // depth within the xml document tree

	// data getters

	public List<Label> getLabels()
	{
		return labels;
	}

	public List<Rule> getRules()
	{
		return rules;
	}

	public Map<Rule, LabelContainer> getRuleToLabel()
	{
		return ruleToLabel;
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
		if (xmlLevel == 2) {
			if (localName.equals(ELEMENT_LABEL)) {
				endLabel();
			}
		} else if (xmlLevel >= 3) {
			if (localName.equals(ELEMENT_RULE)) {
				endRule();
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		xmlLevel++;
		if (xmlLevel == 3) {
			if (localName.equals(ELEMENT_COLOR)) {
				parseColor(attributes);
			} else if (localName.equals(ELEMENT_LABEL)) {
				parseLabel(attributes);
			}
		} else if (xmlLevel >= 4) {
			if (localName.equals(ELEMENT_RULE)) {
				parseRule(attributes);
			}
		}
	}

	private void add(Rule rule, Label label, LabelType type)
	{
		rules.add(rule);
		ruleToLabel.put(rule, new LabelContainer(type, label));
	}

	private LinkedList<Rule> ruleStack = new LinkedList<>();
	private Rule lastStarted = null;

	private void parseRule(Attributes attributes)
	{
		Rule rule;
		if (ruleStack.isEmpty()) {
			rule = new Rule();
		} else {
			Rule top = ruleStack.getLast();
			rule = new Rule(top);
		}
		ruleStack.add(rule);
		lastStarted = rule;

		String valMin = attributes.getValue("min");
		String valMax = attributes.getValue("max");
		String key = attributes.getValue("key");
		if (valMin != null) {
			int value = PrimitiveParser.parseInt(valMin, 0);
			rule.setMinZoom(value);
		}
		if (valMax != null) {
			int value = PrimitiveParser.parseInt(valMax, 0);
			rule.setMaxZoom(value);
		}
		if (key != null) {
			rule.setKey(key);
		}
	}

	private void endRule()
	{
		Rule rule = ruleStack.removeLast();
		if (rule == lastStarted) {
			add(rule, currentLabel, currentType);
		}
		lastStarted = null;
	}

	private Map<String, ColorCode> colorMap = new HashMap<>();

	private void parseColor(Attributes attributes)
	{
		String name = attributes.getValue("name");
		String value = attributes.getValue("value");
		ColorCode color = ColorParser.parseColor(value, null);
		if (color == null) {
			logger.warn(
					"Unable to parse color '" + name + "': '" + value + "'");
			return;
		}
		colorMap.put(name, color);
	}

	private ColorCode parseOrResolveColor(String value)
	{
		ColorCode color = colorMap.get(value);
		if (color != null) {
			return color;
		}
		return ColorParser.parseColor(value, null);
	}

	private Map<String, LabelContainer> namedLabels = new HashMap<>();
	private Label currentLabel = null;
	private LabelType currentType = null;

	private void endLabel()
	{
		currentLabel = null;
		currentType = null;
	}

	private void parseLabel(Attributes attributes)
	{
		// Optional id for inheritance
		String id = attributes.getValue("id");
		// We need either a type or a parent
		String valType = attributes.getValue("type");
		String valParent = attributes.getValue("parent");

		LabelType type = null;
		LabelContainer parent = null;
		if (valParent != null) {
			parent = namedLabels.get(valParent);
			type = parent.getType();
		}
		if (valType != null) {
			type = parseType(valType);
		}

		if (type == null) {
			return;
		}

		Label label = null;
		switch (type) {
		default:
		case PLAIN: {
			if (parent == null) {
				label = new PlainLabel();
			} else if (parent.getType() == LabelType.PLAIN) {
				label = new PlainLabel((PlainLabel) parent.getLabel());
			}
			parsePlain((PlainLabel) label, attributes);
			break;
		}
		case DOT: {
			if (parent == null) {
				label = new DotLabel();
			} else if (parent.getType() == LabelType.DOT) {
				label = new DotLabel((DotLabel) parent.getLabel());
			} else if (parent.getType() == LabelType.PLAIN) {
				label = new DotLabel((PlainLabel) parent.getLabel());
			}
			parseDot((DotLabel) label, attributes);
			break;
		}
		case ICON: {
			if (parent == null) {
				label = new IconLabel();
			} else if (parent.getType() == LabelType.ICON) {
				label = new IconLabel((IconLabel) parent.getLabel());
			} else if (parent.getType() == LabelType.PLAIN) {
				label = new IconLabel((PlainLabel) parent.getLabel());
			}
			parseIcon((IconLabel) label, attributes);
			break;
		}
		}

		currentLabel = label;
		currentType = type;
		if (id != null) {
			namedLabels.put(id, new LabelContainer(type, label));
		}
		labels.add(label);
	}

	private void parsePlain(PlainLabel label, Attributes attributes)
	{
		String valFont = attributes.getValue("font");
		String valFontStyle = attributes.getValue("font-style");
		String valFontSize = attributes.getValue("font-size");
		String valStrokeWidth = attributes.getValue("stroke-width");
		String valFG = attributes.getValue("fg");
		String valBG = attributes.getValue("bg");
		if (valFont != null) {
			FontFamily font = EnumParser.parseFontFamiliy(valFont, null);
			label.setFamily(font);
		}
		if (valFontStyle != null) {
			FontStyle style = EnumParser.parseFontStyle(valFontStyle, null);
			label.setStyle(style);
		}
		if (valFontSize != null) {
			float size = PrimitiveParser.parseFloat(valFontSize, 0f);
			label.setFontSize(size);
		}
		if (valStrokeWidth != null) {
			float width = PrimitiveParser.parseFloat(valStrokeWidth, 0f);
			label.setStrokeWidth(width);
		}
		if (valFG != null) {
			ColorCode color = parseOrResolveColor(valFG);
			label.setFg(color);
		}
		if (valBG != null) {
			ColorCode color = parseOrResolveColor(valBG);
			label.setBg(color);
		}
	}

	private void parseDot(DotLabel label, Attributes attributes)
	{
		parsePlain(label, attributes);
		String valDotFG = attributes.getValue("dot-fg");
		String valRadius = attributes.getValue("radius");
		if (valDotFG != null) {
			ColorCode color = parseOrResolveColor(valDotFG);
			label.setDotFg(color);
		}
		if (valRadius != null) {
			float radius = PrimitiveParser.parseFloat(valRadius, 0f);
			label.setRadius(radius);
		}
	}

	private void parseIcon(IconLabel label, Attributes attributes)
	{
		parsePlain(label, attributes);
		String valImage = attributes.getValue("image");
		String valHeight = attributes.getValue("image-height");
		label.setImage(valImage);
		if (valHeight != null) {
			float height = Float.parseFloat(valHeight);
			label.setIconHeight(height);
		}
	}

	private LabelType parseType(String value)
	{
		if (value.equals("plain")) {
			return LabelType.PLAIN;
		} else if (value.equals("dot")) {
			return LabelType.DOT;
		} else if (value.equals("icon")) {
			return LabelType.ICON;
		}
		logger.warn("Unable to parse type '" + value + "'");
		return null;
	}

}
