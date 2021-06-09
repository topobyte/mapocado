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

package de.topobyte.mapocado.styles.directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.styles.classes.ClassFileHandler;
import de.topobyte.mapocado.styles.classes.ClassFileReader;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.labels.LabelFileHandler;
import de.topobyte.mapocado.styles.labels.LabelFileReader;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.Rule;

public class StyleDirectory
{

	private boolean initialized = false;

	private File dir;
	private ColorCode backgroundColor;
	private ColorCode mapOverlayInner;
	private ColorCode mapOverlayOuter;
	private ColorCode mapOverlayGpsInner;
	private ColorCode mapOverlayGpsOuter;
	private List<ObjectClass> objectClasses;

	private List<Rule> labelRules;
	private Map<Rule, LabelContainer> labelStyles;

	private File patterns;
	private File symbols;

	public StyleDirectory(File dir)
	{
		this.dir = dir;
	}

	public void init()
			throws ParserConfigurationException, SAXException, IOException
	{
		File fileClasses = new File(dir, "classes.xml");
		FileInputStream fis = new FileInputStream(fileClasses);
		ClassFileHandler classFileHandler = ClassFileReader.read(fis);
		fis.close();

		File fileLabels = new File(dir, "labels.xml");
		fis = new FileInputStream(fileLabels);
		LabelFileHandler labelFileHandler = LabelFileReader.read(fis);
		fis.close();

		backgroundColor = classFileHandler.getBackground();
		mapOverlayInner = classFileHandler.getOverlayInner();
		mapOverlayOuter = classFileHandler.getOverlayOuter();
		mapOverlayGpsInner = classFileHandler.getOverlayGpsInner();
		mapOverlayGpsOuter = classFileHandler.getOverlayGpsOuter();
		objectClasses = classFileHandler.getObjectClasses();

		labelRules = labelFileHandler.getRules();
		labelStyles = labelFileHandler.getRuleToLabel();

		patterns = new File(dir, "patterns");
		symbols = new File(dir, "symbols");

		initialized = true;
	}

	public ColorCode getBackgroundColor()
	{
		return backgroundColor;
	}

	public ColorCode getMapOverlayInner()
	{
		return mapOverlayInner;
	}

	public ColorCode getMapOverlayOuter()
	{
		return mapOverlayOuter;
	}

	public ColorCode getMapOverlayGpsInner()
	{
		return mapOverlayGpsInner;
	}

	public ColorCode getMapOverlayGpsOuter()
	{
		return mapOverlayGpsOuter;
	}

	public List<ObjectClass> getObjectClasses()
	{
		return objectClasses;
	}

	public boolean isInitialized()
	{
		return initialized;
	}

	public File getPattern(String name)
	{
		return new File(patterns, name);
	}

	public File getSymbol(String name)
	{
		return new File(symbols, name);
	}

	public List<Rule> getLabelRules()
	{
		return labelRules;
	}

	public Map<Rule, LabelContainer> getLabelStyles()
	{
		return labelStyles;
	}

	public File getPatternsDir()
	{
		return patterns;
	}

	public File getSymbolsDir()
	{
		return symbols;
	}

}
