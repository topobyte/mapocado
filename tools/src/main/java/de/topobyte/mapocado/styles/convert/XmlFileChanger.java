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

package de.topobyte.mapocado.styles.convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.misc.ColorParser;

public class XmlFileChanger
{
	final static Logger logger = LoggerFactory.getLogger(XmlFileChanger.class);

	private Document docInput;
	private Set<String> elementTypes = new HashSet<>();

	private ColorConverter converter;

	public XmlFileChanger(ColorConverter converter)
	{
		this.converter = converter;
	}

	public void setElementTypes(Set<String> types)
	{
		for (String type : types) {
			elementTypes.add(type);
		}
	}

	private void edit()
	{
		Element element = docInput.getDocumentElement();

		// start recursive editing
		edit(element);
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
		NamedNodeMap attributes = node.getAttributes();
		String nodeName = node.getNodeName();
		if (!elementTypes.contains(nodeName)) {
			return;
		}
		for (int i = 0; i < attributes.getLength(); i++) {
			Attr attribute = (Attr) attributes.item(i);
			String val = attribute.getNodeValue();
			boolean valid = ColorParser.isValidColor(val);
			if (!valid) {
				continue;
			}
			ColorConverter c = getConverter(node, attribute.getName());
			ColorCode colorCode = ColorParser.parseColor(val, null);
			ConversionContext context = new ConversionContext();
			ColorCode newColor = c.convert(context, colorCode);

			String oldValue = ColorParser.colorString(colorCode);
			String newValue = ColorParser.colorString(newColor);
			logger.debug(String.format("0x%s -> 0x%s", oldValue, newValue));

			attribute.setNodeValue("#" + newValue);
		}
	}

	protected ColorConverter getConverter(Node node, String attribute)
	{
		return converter;
	}

	public void execute(String pathInput, String pathOutput)
			throws SAXException, IOException, ParserConfigurationException,
			TransformerException
	{
		FileInputStream input = new FileInputStream(new File(pathInput));
		FileOutputStream output = new FileOutputStream(new File(pathOutput));
		execute(input, output);
		input.close();
		output.close();
	}

	public void execute(InputStream input, OutputStream output)
			throws ParserConfigurationException, SAXException, IOException,
			TransformerException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		docInput = builder.parse(input);

		SAXTransformerFactory saxFactory = (SAXTransformerFactory) TransformerFactory
				.newInstance();
		TransformerHandler handler = saxFactory.newTransformerHandler();
		Transformer serializer = handler.getTransformer();
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		edit();

		StreamResult resultRules = new StreamResult(output);
		handler.setResult(resultRules);

		DOMSource domSourceRules = new DOMSource(docInput.getDocumentElement());
		serializer.transform(domSourceRules, resultRules);
	}
}
