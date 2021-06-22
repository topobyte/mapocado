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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.topobyte.mapocado.styles.labels.elements.DotLabel;
import de.topobyte.mapocado.styles.labels.elements.IconLabel;
import de.topobyte.mapocado.styles.labels.elements.Label;
import de.topobyte.mapocado.styles.labels.elements.PlainLabel;

public class LabelInfo
{

	static final Logger logger = LoggerFactory.getLogger(LabelInfo.class);

	public void execute(Path pathInput)
			throws ParserConfigurationException, SAXException, IOException
	{
		System.out.println("input: " + pathInput);

		LabelFileHandler handler = LabelFileReader
				.read(Files.newInputStream(pathInput));
		List<Label> labels = handler.getLabels();
		for (Label label : labels) {
			print(label);
		}
	}

	private static void print(Label label)
	{
		if (label instanceof DotLabel) {
			print((DotLabel) label);
		} else if (label instanceof IconLabel) {
			print((IconLabel) label);
		} else if (label instanceof PlainLabel) {
			print((PlainLabel) label);
		}
	}

	private static void print(PlainLabel label)
	{
		System.out.println(String.format(
				"[plain] family '%s', style '%s', font-size '%04.1f', fg '%s', bg '%s', stroke width '%s'",
				label.getFamily(), label.getStyle(), label.getFontSize(),
				label.getFg(), label.getBg(), label.getStrokeWidth()));
	}

	private static void print(DotLabel label)
	{
		System.out.println(String.format(
				"[dot]   family '%s', style '%s', font-size '%.1f', fg '%s', bg '%s', stroke width '%s', dot-fg '%s', radius '%.1f'",
				label.getFamily(), label.getStyle(), label.getFontSize(),
				label.getFg(), label.getBg(), label.getStrokeWidth(),
				label.getDotFg(), label.getRadius()));
	}

	private static void print(IconLabel label)
	{
		System.out.println(String.format(
				"[icon]  family '%s', style '%s', font-size '%.1f', fg '%s', bg '%s', stroke width '%s', image '%s', image-height '%.1f'",
				label.getFamily(), label.getStyle(), label.getFontSize(),
				label.getFg(), label.getBg(), label.getStrokeWidth(),
				label.getImage(), label.getIconHeight()));
	}

}
