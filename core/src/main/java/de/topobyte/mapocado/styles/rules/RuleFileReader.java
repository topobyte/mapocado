package de.topobyte.mapocado.styles.rules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class RuleFileReader
{

	public static void read(RuleSet config, String filename)
			throws ParserConfigurationException, SAXException, IOException
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		RuleFileHandler handler = new RuleFileHandler(config);
		File file = new File(filename);

		if (file.exists() && file.canRead()) {
			parser.parse(file, handler);
		} else {
			InputStream is = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(filename);
			parser.parse(is, handler);
		}
	}

	public static void read(RuleSet config, InputStream input)
			throws ParserConfigurationException, SAXException, IOException
	{
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		RuleFileHandler handler = new RuleFileHandler(config);

		parser.parse(input, handler);
	}

}