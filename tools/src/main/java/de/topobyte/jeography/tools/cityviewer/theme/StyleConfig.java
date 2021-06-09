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

package de.topobyte.jeography.tools.cityviewer.theme;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class StyleConfig
{

	private List<Style> styles = new ArrayList<>();

	public List<Style> getStyles()
	{
		return styles;
	}

	public static StyleConfig parse(URL url)
			throws IOException, ParserConfigurationException, SAXException
	{
		StyleConfig styleConfig = new StyleConfig();
		InputStream input = url.openStream();
		SAXParser sax = SAXParserFactory.newInstance().newSAXParser();
		StylesHandler configHandler = new StylesHandler(styleConfig);
		sax.parse(input, configHandler);
		return styleConfig;
	}

	private static class StylesHandler extends DefaultHandler
	{
		private StyleConfig styleConfig;
		private int level = 0;

		public StylesHandler(StyleConfig styleConfig)
		{
			this.styleConfig = styleConfig;
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException
		{
			level++;
			if (level == 2 && qName.equals("style")) {
				String name = attributes.getValue("name");
				String file = attributes.getValue("file");
				styleConfig.styles.add(new Style(name, file));
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException
		{
			level--;
		}

	}
}
