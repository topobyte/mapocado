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

package de.topobyte.mapocado.styles.visualize.html;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.HtmlBuilder;
import de.topobyte.jsoup.components.Body;
import de.topobyte.jsoup.components.Div;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.rules.Rule;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.mapocado.styles.rules.enums.ElementType;
import de.topobyte.mapocado.styles.rules.enums.Simplification;
import de.topobyte.mapocado.styles.rules.match.MatchingValues;

public class RuleFileCreator
{

	private final RuleSet ruleSet;
	private final Map<String, ObjectClass> refToClass;

	private HtmlBuilder builder;
	private Body body;

	private boolean collapseOnStart = true;
	private int ids = 1; // id counter for child elements

	public RuleFileCreator(RuleSet ruleSet, Map<String, ObjectClass> refToClass)
	{
		this.ruleSet = ruleSet;
		this.refToClass = refToClass;
	}

	public void createRulesFile(File fileRules) throws TransformerException,
			IOException, ParserConfigurationException
	{
		builder = new HtmlBuilder();
		body = builder.getBody();

		List<Rule> rules = ruleSet.getRules();
		for (Rule rule : rules) {
			Div ruleElement = createRuleLine(rule, 0);
			body.ac(ruleElement);
		}

		builder.write(fileRules);
	}

	private Div createRuleLine(Rule rule, int depth)
	{
		String position = "";
		if (depth > 0) {
			position = "position:relative;left:20px";
		}
		String id = "c" + ids++;

		Div div = HTML.div().attr("style", "" + position);
		String text = createRuleText(rule);
		Div button = createExpandButton(id);
		Div childrenDiv = HTML.div().attr("id", id).attr("style",
				collapseOnStart ? "display:none" : "display:block");

		div.ac(button);
		div.append(text);
		div.ac(childrenDiv);

		List<ObjectClassRef> classes = rule.getClasses();
		for (ObjectClassRef ref : classes) {
			Div refElement = createRefLine(ref);
			childrenDiv.ac(refElement);
		}

		List<Rule> children = rule.getRules();
		for (Rule child : children) {
			Div childElement = createRuleLine(child, depth + 1);
			childrenDiv.ac(childElement);
		}
		return div;
	}

	private Div createExpandButton(String id)
	{
		return HTML.div().attr("style",
				"float:left; width:16px; height:16px; background-color:#999999; margin-right:2px")
				.attr("onclick", getVisiblityCode(id));
	}

	private String getVisiblityCode(String id)
	{
		return "javascript: element = document.getElementById('" + id + "');"
				+ "d = element.style.display; "
				+ "if (d=='none') {element.style.display = 'block';}"
				+ " else {element.style.display = 'none'}";
	}

	private Div createRefLine(ObjectClassRef classRef)
	{
		String ref = classRef.getRef();
		Simplification simplification = classRef.getSimplification();
		int min = classRef.getMinZoom();
		int max = classRef.getMaxZoom();

		Div div = HTML.div().attr("style", "position:relative;left:20px");
		div.append(String.format("<b>ref</b>: %s, min: %d max: %d", ref, min,
				max));
		div.ac(HTML.br());
		if (simplification != Simplification.NONE) {
			div.at("simplify");
			div.ac(HTML.br());
		}
		Set<String> mandatoryKeys = classRef.getMandatoryKeepKeys();
		if (mandatoryKeys.size() > 0) {
			div.at("Mandatory Keeping keys: " + mandatoryKeys.toString());
			div.ac(HTML.br());
		}
		Set<String> optionalKeys = classRef.getOptionalKeepKeys();
		if (optionalKeys.size() > 0) {
			div.at("Optional Keeping keys: " + optionalKeys.toString());
			div.ac(HTML.br());
		}

		ObjectClass objectClass = refToClass.get(ref);

		if (objectClass == null) {
			return div;
		}

		if (objectClass.elements.length > 0) {
			Div renderElements = HTML.div().attr("style",
					"position:relative;left:20px;border:1px solid black;padding:2px");
			div.ac(renderElements);
			for (RenderElement render : objectClass.elements) {
				String metaLine = ClassFileCreator
						.createObjectClassLine(builder, objectClass);
				Div line = ClassFileCreator.createElementLine(builder, render);
				renderElements.at(metaLine);
				renderElements.ac(HTML.br());
				renderElements.ac(line);
				renderElements.ac(HTML.br().attr("style", "clear:both"));
			}
		}

		return div;
	}

	private String createRuleText(Rule rule)
	{
		ElementType type = rule.getElementType();
		MatchingValues keys = rule.getKeys();
		MatchingValues values = rule.getValues();

		StringBuffer buffer = new StringBuffer();

		String typename = type.toString();
		buffer.append(typename);
		buffer.append(" ");

		if (keys.hasWildcard()) {
			buffer.append("<b>keys</b>: *");
		} else {
			buffer.append("<b>keys</b>: ");
			Iterator<String> iterator = keys.getValues().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				buffer.append(key);
				if (iterator.hasNext()) {
					buffer.append(", ");
				}
			}
		}
		buffer.append(" ");

		if (values.hasWildcard()) {
			buffer.append("<b>values</b>: *");
		} else {
			buffer.append("<b>values</b>: ");
			Iterator<String> iterator = values.getValues().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				buffer.append(key);
				if (iterator.hasNext()) {
					buffer.append(", ");
				}
			}
		}

		return buffer.toString();
	}
}
