package de.topobyte.mapocado.styles.convert;

import java.util.HashSet;
import java.util.Set;

public class LabelFileChanger extends XmlFileChanger
{

	public LabelFileChanger(ColorConverter converter)
	{
		super(converter);
		Set<String> elementTypes = new HashSet<>();
		elementTypes.add("color");
		elementTypes.add("label");
		setElementTypes(elementTypes);
	}

}
