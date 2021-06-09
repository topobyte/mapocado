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