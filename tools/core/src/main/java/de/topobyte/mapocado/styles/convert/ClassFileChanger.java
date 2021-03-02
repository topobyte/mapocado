package de.topobyte.mapocado.styles.convert;

import java.util.HashSet;
import java.util.Set;

public class ClassFileChanger extends XmlFileChanger
{

	public ClassFileChanger(ColorConverter converter)
	{
		super(converter);
		Set<String> elementTypes = new HashSet<>();
		elementTypes.add("classes");
		elementTypes.add("area");
		elementTypes.add("caption");
		elementTypes.add("circle");
		elementTypes.add("line");
		elementTypes.add("lineSymbol");
		elementTypes.add("pathText");
		elementTypes.add("symbol");
		setElementTypes(elementTypes);
	}

}
