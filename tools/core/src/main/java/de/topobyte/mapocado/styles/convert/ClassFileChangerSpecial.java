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

package de.topobyte.mapocado.styles.convert;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class ClassFileChangerSpecial extends ClassFileChanger
{
	private List<String> nodeNames = new ArrayList<>();
	private List<String> attributeNames = new ArrayList<>();

	private ColorConverter converterAlt;

	public ClassFileChangerSpecial(ColorConverter converter,
			ColorConverter converterAlt, String... args)
	{
		super(converter);
		this.converterAlt = converterAlt;
		for (int i = 0; i < args.length; i += 2) {
			nodeNames.add(args[i]);
			attributeNames.add(args[i + 1]);
		}
	}

	@Override
	protected ColorConverter getConverter(Node node, String attribute)
	{
		for (int i = 0; i < nodeNames.size(); i++) {
			if (node.getNodeName().equals(nodeNames.get(i))
					&& attribute.equals(attributeNames.get(i))) {
				return converterAlt;
			}
		}
		return super.getConverter(node, attribute);
	}
}
