package de.topobyte.mapocado.styles.labels;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class LabelFileReader
{

	public static LabelFileHandler read(InputStream input)
			throws ParserConfigurationException, SAXException, IOException
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		LabelFileHandler handler = new LabelFileHandler();
		xmlReader.setContentHandler(handler);

		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		xmlReader.parse(new InputSource(input));

		return handler;
	}

}