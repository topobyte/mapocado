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

package de.topobyte.mapocado.styles.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.rules.enums.ClosedType;
import de.topobyte.mapocado.styles.rules.enums.ElementType;
import de.topobyte.mapocado.styles.rules.match.MatchingValues;

public class Rule
{

	private static final Pattern SPLITTER = Pattern.compile("\\|");
	private static final String NEGATION = "~";
	private static final String WILDCARD = "*";

	private final ElementType elementType;
	private final ClosedType closedType;
	private final int zoomMin;
	private final int zoomMax;
	private final String valK;
	private final String valV;

	private List<Rule> rules = new ArrayList<>();
	private List<ObjectClassRef> elements = new ArrayList<>();

	private boolean negative = false;
	private MatchingValues keys = new MatchingValues();
	private MatchingValues values = new MatchingValues();

	public Rule(ElementType elementType, ClosedType closedType, int zoomMin,
			int zoomMax, String valK, String valV)
	{
		this.elementType = elementType;
		this.closedType = closedType;
		this.zoomMin = zoomMin;
		this.zoomMax = zoomMax;
		this.valK = valK;
		this.valV = valV;

		String[] valuesKeys = SPLITTER.split(valK);
		for (String value : valuesKeys) {
			if (value.equals(WILDCARD)) {
				keys.setHasWildcard(true);
			} else {
				keys.add(value);
			}
		}

		String[] valuesVals = SPLITTER.split(valV);
		for (String value : valuesVals) {
			if (value.equals(NEGATION)) {
				negative = true;
			} else if (value.equals(WILDCARD)) {
				values.setHasWildcard(true);
			} else {
				values.add(value);
			}
		}
	}

	public void addRule(Rule rule)
	{
		rules.add(rule);
	}

	public void addElement(ObjectClassRef element)
	{
		elements.add(element);
	}

	@Override
	public String toString()
	{
		return String.format("%s %s %s", elementType.name(), valK, valV);
	}

	public ElementType getElementType()
	{
		return elementType;
	}

	public ClosedType getClosedType()
	{
		return closedType;
	}

	public int getZoomMin()
	{
		return zoomMin;
	}

	public int getZoomMax()
	{
		return zoomMax;
	}

	public List<Rule> getRules()
	{
		return rules;
	}

	public List<ObjectClassRef> getClasses()
	{
		return elements;
	}

	public boolean isNegative()
	{
		return negative;
	}

	public MatchingValues getKeys()
	{
		return keys;
	}

	public MatchingValues getValues()
	{
		return values;
	}
}
