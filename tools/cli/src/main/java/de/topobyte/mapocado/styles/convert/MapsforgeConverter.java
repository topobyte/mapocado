package de.topobyte.mapocado.styles.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.topobyte.mapocado.styles.ZoomRestriction;
import de.topobyte.mapocado.swing.viewer.MapViewerCompare;
import de.topobyte.utilities.apache.commons.cli.OptionHelper;

/**
 * Convert a mapsforge style to a style/class configuration
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 */
public class MapsforgeConverter
{
	static final Logger logger = LoggerFactory
			.getLogger(MapViewerCompare.class);

	private static final String OPTION_INPUT = "input";
	private static final String OPTION_OUTPUT_RULES = "rules";
	private static final String OPTION_OUTPUT_CLASSES = "classes";

	/**
	 * @param args
	 *            input, output_index, output_data
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws TransformerException
	 */
	public static void main(String[] args) throws SAXException, IOException,
			ParserConfigurationException, TransformerException
	{
		Options options = new Options();
		OptionHelper.addL(options, OPTION_INPUT, true, true,
				"a rendering configuration file");
		OptionHelper.addL(options, OPTION_OUTPUT_RULES, true, true,
				"where to put the rules file");
		OptionHelper.addL(options, OPTION_OUTPUT_CLASSES, true, true,
				"where to put the classes file");

		CommandLine line = null;
		try {
			line = new DefaultParser().parse(options, args);
		} catch (ParseException e) {
			System.out
					.println("unable to parse command line: " + e.getMessage());
		}

		if (line == null) {
			return;
		}

		String pathInput = line.getOptionValue(OPTION_INPUT);
		String pathClasses = line.getOptionValue(OPTION_OUTPUT_CLASSES);
		String pathRules = line.getOptionValue(OPTION_OUTPUT_RULES);

		System.out.println("input: " + pathInput);
		System.out.println("output classes: " + pathClasses);
		System.out.println("output rules: " + pathRules);

		MapsforgeConverter converter = new MapsforgeConverter();
		converter.execute(pathInput, pathClasses, pathRules);
	}

	private void execute(String pathInput, String pathClasses, String pathRules)
			throws SAXException, IOException, ParserConfigurationException,
			TransformerException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		docInput = builder.parse(new File(pathInput));

		FileOutputStream outputClasses = new FileOutputStream(pathClasses);
		FileOutputStream outputRules = new FileOutputStream(pathRules);

		SAXTransformerFactory saxFactory = (SAXTransformerFactory) TransformerFactory
				.newInstance();
		TransformerHandler handler = saxFactory.newTransformerHandler();
		Transformer serializer = handler.getTransformer();
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		docClasses = builder.newDocument();

		edit();

		StreamResult resultRules = new StreamResult(outputRules);
		handler.setResult(resultRules);

		DOMSource domSourceRules = new DOMSource(docInput.getDocumentElement());
		serializer.transform(domSourceRules, resultRules);

		StreamResult resultClasses = new StreamResult(outputClasses);
		handler.setResult(resultClasses);

		DOMSource domSourceClasses = new DOMSource(docClasses);
		serializer.transform(domSourceClasses, resultClasses);
	}

	private Document docInput;
	private Document docClasses;
	private int id = 1;
	private List<RuleEntry> rules = new ArrayList<>();
	private List<Node> classes = new ArrayList<>();
	private List<Node> whitespace = new ArrayList<>();
	private List<Node> toPurge = new ArrayList<>();

	private void edit()
	{
		Element element = docInput.getDocumentElement();

		// start recursive editing
		edit(element);
		// remove elements to purge
		purgeRulesFromInput();
		// create styles file
		createStylesDoc();
		// prettify whitespace
		prettifyWhitespace();
	}

	private void prettifyWhitespace()
	{
		// for (Node clazz : classes) {
		// Node previous = clazz.getPreviousSibling();
		// if (previous != null) {
		// previous.getParentNode().removeChild(previous);
		// }
		// }
		for (Node node : whitespace) {
			Node parent = node.getParentNode();
			parent.removeChild(node);
		}
	}

	private void purgeRulesFromInput()
	{
		for (RuleEntry entry : rules) {
			Node rule = entry.rule.getParentNode();
			rule.removeChild(entry.rule);
		}
	}

	private void createStylesDoc()
	{
		Element styles = docClasses.createElement("classes");
		docClasses.appendChild(styles);
		for (RuleEntry entry : rules) {
			Element style = docClasses.createElement("class");
			style.setAttribute("id", "" + entry.id);
			styles.appendChild(style);
			if (entry.zoomRestriction.getMinZoom() != -1) {
				style.setAttribute("zoom-min",
						"" + entry.zoomRestriction.getMinZoom());
			}
			if (entry.zoomRestriction.getMaxZoom() != -1) {
				style.setAttribute("zoom-max",
						"" + entry.zoomRestriction.getMaxZoom());
			}
			Node copy = docClasses.importNode(entry.rule, false);
			style.appendChild(copy);
		}
	}

	private void edit(Node node)
	{
		check(node);

		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			edit(child);
		}
	}

	private void check(Node node)
	{
		String nodeName = node.getNodeName();
		if (nodeName.equals("#text")) {
			whitespace.add(node);
		} else if (nodeName.equals("area")) {
			replace(node);
		} else if (nodeName.equals("line")) {
			replace(node);
		} else if (nodeName.equals("caption")) {
			replace(node);
		} else if (nodeName.equals("symbol")) {
			replace(node);
		} else if (nodeName.equals("pathText")) {
			replace(node);
		} else if (nodeName.equals("circle")) {
			replace(node);
		} else if (nodeName.equals("lineSymbol")) {
			replace(node);
		}
	}

	private void replace(Node node)
	{
		Node rule = node.getParentNode();

		Element clazz = docInput.createElement("class");
		classes.add(clazz);
		int classId = id++;
		clazz.setAttribute("ref", "" + classId);
		ZoomRestriction zoomRestriction = deduceZoomResctrictions(node);
		rule.appendChild(clazz);

		String nodeName = node.getNodeName();
		if (nodeName.equals("pathText") || nodeName.equals("caption")) {
			Element keep = docInput.createElement("keep");
			clazz.appendChild(keep);
			NamedNodeMap attributes = node.getAttributes();
			String key = attributes.getNamedItem("k").getTextContent();
			keep.setAttribute("key", key);
		}

		toPurge.add(clazz.getPreviousSibling());
		rules.add(new RuleEntry(node, classId, zoomRestriction));
	}

	private ZoomRestriction deduceZoomResctrictions(Node node)
	{
		int min = -1, max = -1;
		Node iter = node.getParentNode();
		while (iter != null) {
			if (iter.getNodeName().equals("rule")) {
				NamedNodeMap attributes = iter.getAttributes();
				if (attributes.getNamedItem("zoom-min") != null) {
					String minZoom = attributes.getNamedItem("zoom-min")
							.getTextContent();
					int cmin = Integer.parseInt(minZoom);
					if (min == -1 || cmin > min) {
						min = cmin;
					}
				}
				if (attributes.getNamedItem("zoom-max") != null) {
					String maxZoom = attributes.getNamedItem("zoom-max")
							.getTextContent();
					int cmax = Integer.parseInt(maxZoom);
					if (max == -1 || cmax > max) {
						max = cmax;
					}
				}
			}
			iter = iter.getParentNode();
		}
		return new ZoomRestriction(min, max);
	}

	private class RuleEntry
	{

		private final Node rule;
		private final int id;
		private final ZoomRestriction zoomRestriction;

		public RuleEntry(Node rule, int id, ZoomRestriction zoomRestriction)
		{
			this.rule = rule;
			this.id = id;
			this.zoomRestriction = zoomRestriction;
		}

	}
}
