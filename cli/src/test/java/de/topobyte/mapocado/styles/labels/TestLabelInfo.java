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
import java.nio.file.Path;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.system.utils.SystemPaths;

public class TestLabelInfo
{

	public static void main(String[] args)
			throws SAXException, IOException, ParserConfigurationException
	{
		Path pathInput = SystemPaths.CWD.getParent().resolve(
				"config/citymaps/rendertheme-v2/styles/default/labels.xml");

		LabelInfo task = new LabelInfo();
		task.execute(pathInput);
	}

}
