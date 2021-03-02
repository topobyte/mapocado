package de.topobyte.mapocado.styles.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ClassFileReader
{

	public static ClassFileHandler read(String filename)
			throws ParserConfigurationException, SAXException, IOException
	{
		File file = new File(filename);

		InputStream is = null;
		if (file.exists() && file.canRead()) {
			is = new FileInputStream(file);
		} else {
			is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(filename);
		}
		ClassFileHandler classFileHandler = read(is);
		is.close();
		return classFileHandler;
	}

	public static ClassFileHandler read(InputStream input)
			throws ParserConfigurationException, SAXException, IOException
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		XMLReader xmlReader = parser.getXMLReader();
		ClassFileHandler handler = new ClassFileHandler();
		xmlReader.setContentHandler(handler);

		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		// parser.parse(input, handler);
		xmlReader.parse(new InputSource(input));

		return handler;
	}

}