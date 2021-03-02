package de.topobyte.mapocado.mapformat.rtree.str;

import java.util.List;

public class STRConstructionNode<T>
{

	private final List<STRConstructionElement<T>> elements;

	public STRConstructionNode(List<STRConstructionElement<T>> elements)
	{
		this.elements = elements;
	}

	public List<STRConstructionElement<T>> getElements()
	{
		return elements;
	}

}
