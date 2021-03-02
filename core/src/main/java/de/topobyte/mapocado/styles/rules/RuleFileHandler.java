package de.topobyte.mapocado.styles.rules;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.topobyte.mapocado.styles.ZoomRestriction;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.rules.enums.ClosedType;
import de.topobyte.mapocado.styles.rules.enums.ElementType;
import de.topobyte.mapocado.styles.rules.enums.Simplification;

public class RuleFileHandler extends DefaultHandler
{

	private RuleSet ruleSet;

	public RuleFileHandler(RuleSet ruleSet)
	{
		this.ruleSet = ruleSet;
	}

	public RuleSet getRuleSet()
	{
		return ruleSet;
	}

	@Override
	public void startDocument() throws SAXException
	{

	}

	@Override
	public void endDocument() throws SAXException
	{

	}

	private static String ELEMENT_RULES = "rules";
	private static String ELEMENT_RULE = "rule";
	private static String ELEMENT_CLASS = "class";
	private static String ELEMENT_KEEP = "keep";

	private int xmlLevel = 0;
	private Stack<Rule> ruleStack = new Stack<>();
	private ObjectClassRef currentClass = null;

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		xmlLevel--;
		if (qName.equals(ELEMENT_RULE)) {
			ruleStack.pop();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		xmlLevel++;
		if (xmlLevel == 1) {
			if (qName.equals(ELEMENT_RULES)) {
				// ignore, nothing to parse here
			}
		} else {
			// level > 1
			if (qName.equals(ELEMENT_RULE)) {
				parseRule(attributes);
			} else if (qName.equals(ELEMENT_CLASS)) {
				parseClass(attributes);
			} else if (qName.equals(ELEMENT_KEEP)) {
				parseKeep(attributes);
			}
		}
	}

	private static ElementType DEFAULT_ELEMENT_TYPE = ElementType.ANY;
	private static ClosedType DEFAULT_CLOSED_TYPE = ClosedType.ANY;
	private static int DEFAULT_ZOOM_MIN = 0;
	private static int DEFAULT_ZOOM_MAX = 127;

	private static ElementType parseElementType(String val)
	{
		if (val.equals("node")) {
			return ElementType.NODE;
		} else if (val.equals("way")) {
			return ElementType.WAY;
		} else {
			return DEFAULT_ELEMENT_TYPE;
		}
	}

	private static ClosedType parseClosedType(String val)
	{
		if (val == null) {
			return DEFAULT_CLOSED_TYPE;
		} else if (val.equals("yes")) {
			return ClosedType.YES;
		} else if (val.equals("no")) {
			return ClosedType.NO;
		} else {
			return DEFAULT_CLOSED_TYPE;
		}
	}

	private static int parseInt(String val, int defaultValue)
	{
		if (val == null) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(val);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	private void parseRule(Attributes attributes)
	{
		String valE = attributes.getValue("e");
		String valK = attributes.getValue("k");
		String valV = attributes.getValue("v");
		String valClosed = attributes.getValue("closed");
		String valZoomMin = attributes.getValue("zoom-min");
		String valZoomMax = attributes.getValue("zoom-max");

		ElementType elementType = parseElementType(valE);
		ClosedType closedType = parseClosedType(valClosed);
		int zoomMin = parseInt(valZoomMin, DEFAULT_ZOOM_MIN);
		int zoomMax = parseInt(valZoomMax, DEFAULT_ZOOM_MAX);

		Rule rule = new Rule(elementType, closedType, zoomMin, zoomMax, valK,
				valV);

		if (ruleStack.isEmpty()) {
			ruleSet.addRule(rule);
		} else {
			ruleStack.peek().addRule(rule);
		}
		ruleStack.push(rule);
	}

	private void parseClass(Attributes attributes)
	{
		String id = attributes.getValue("id");
		String valSimplify = attributes.getValue("simplify");
		Simplification simplification = parseSimplification(valSimplify);

		ObjectClassRef objectClass = new ObjectClassRef(id, simplification);
		if (ruleSet.hasObjectClass(objectClass.getRef())) {
			objectClass = ruleSet.getObjectClassRef(objectClass.getRef());
		} else {
			ZoomRestriction zoomRestriction = getCurrentZoomRestriction();
			// if (zoomRestriction.getMinZoom() != DEFAULT_ZOOM_MIN
			// || zoomRestriction.getMaxZoom() != DEFAULT_ZOOM_MAX) {
			// System.out.println(zoomRestriction);
			// }
			objectClass.setMinZoom(zoomRestriction.getMinZoom());
			objectClass.setMaxZoom(zoomRestriction.getMaxZoom());
			ruleSet.addObjectClass(objectClass);
		}
		ruleStack.peek().addElement(objectClass);
		currentClass = objectClass;
	}

	private Simplification parseSimplification(String value)
	{
		Simplification s = Simplification.NONE;
		if (value == null) {
			return s;
		}
		if (value.equals("node")) {
			s = Simplification.NODE;
		}
		return s;
	}

	private void parseKeep(Attributes attributes)
	{
		String k = attributes.getValue("key");
		boolean mandatory = true;
		String sMandatory = attributes.getValue("mandatory");
		if (sMandatory != null) {
			if (sMandatory.toLowerCase().equals("false")) {
				mandatory = false;
			}
		}
		if (mandatory) {
			currentClass.getMandatoryKeepKeys().add(k);
		} else {
			currentClass.getOptionalKeepKeys().add(k);
		}
	}

	private ZoomRestriction getCurrentZoomRestriction()
	{
		Rule root = ruleStack.get(0);
		ZoomRestriction restriction = new ZoomRestriction(root);
		for (int i = 1; i < ruleStack.size(); i++) {
			Rule rule = ruleStack.get(i);
			restriction = restriction.getNewRestriction(rule);
		}
		return restriction;
	}

}
