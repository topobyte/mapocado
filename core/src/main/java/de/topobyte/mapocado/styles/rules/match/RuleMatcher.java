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

package de.topobyte.mapocado.styles.rules.match;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.topobyte.mapocado.mapformat.model.Closeable;
import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.styles.ZoomRestriction;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.rules.Rule;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.mapocado.styles.rules.enums.ClosedType;
import de.topobyte.mapocado.styles.rules.enums.ElementType;

public class RuleMatcher
{

	private List<Rule> rules = new ArrayList<>();

	public RuleMatcher(RuleSet rules)
	{
		List<Rule> ruleList = rules.getRules();
		for (Rule rule : ruleList) {
			this.rules.add(rule);
		}
	}

	// we need the raw tags here since
	public Set<ObjectClassRef> getElements(Entity entity,
			Map<String, String> tags, int minZoom, int maxZoom,
			boolean checkTags)
	{
		ElementType elementType = getEntityType(entity);

		boolean closed = false;
		if (elementType == ElementType.WAY) {
			closed = ((Closeable) entity).isClosed();
		}

		Set<ObjectClassRef> elements = new HashSet<>();

		// TIntObjectHashMap<String> tags = entity.getTags();

		for (Rule rule : rules) {
			ZoomRestriction zoomRestriction = new ZoomRestriction(rule);
			check(rule, elementType, closed, tags, elements, minZoom, maxZoom,
					zoomRestriction, checkTags);
		}

		return elements;
	}

	private ElementType getEntityType(Entity entity)
	{
		if (entity instanceof Node) {
			return ElementType.NODE;
		} else if (entity instanceof Way) {
			return ElementType.WAY;
		} else if (entity instanceof Relation) {
			return ElementType.WAY;
		}
		return null;
	}

	private void check(Rule rule, ElementType elementType, boolean closed,
			Map<String, String> tags, Set<ObjectClassRef> elements, int minZoom,
			int maxZoom, ZoomRestriction zoomRestriction, boolean checkTags)
	{
		if (fits(rule, elementType, closed, tags, minZoom, maxZoom)) {

			if (!checkTags) {
				elements.addAll(rule.getClasses());
			} else {
				// ignore those objectRefs that the element does not carry the
				// required keep-key for.
				List<ObjectClassRef> taken = new ArrayList<>();
				List<ObjectClassRef> classes = rule.getClasses();
				for (ObjectClassRef classRef : classes) {
					boolean take = true;
					Set<String> keepKeys = classRef.getMandatoryKeepKeys();
					for (String keepKey : keepKeys) {
						if (!tags.containsKey(keepKey)) {
							take = false;
						}
					}
					if (take) {
						taken.add(classRef);
					}
				}
				elements.addAll(taken);
			}

			// set zoom restrictions for class reference
			for (ObjectClassRef classRef : rule.getClasses()) {
				classRef.setMinZoom(zoomRestriction.getMinZoom());
				classRef.setMaxZoom(zoomRestriction.getMaxZoom());
			}
			for (Rule sub : rule.getRules()) {
				ZoomRestriction newZoomRestriction = zoomRestriction
						.getNewRestriction(sub);
				check(sub, elementType, closed, tags, elements, minZoom,
						maxZoom, newZoomRestriction, checkTags);
			}
		}
	}

	private boolean fits(Rule rule, ElementType elementType, boolean closed,
			Map<String, String> tags, int minZoom, int maxZoom)
	{
		if (rule.getElementType() != elementType) {
			return false;
		}
		// @formatter:off
		boolean fit = (maxZoom <= rule.getZoomMax() && maxZoom >= rule.getZoomMin())
			|| (minZoom <= rule.getZoomMax() && minZoom >= rule.getZoomMin())
			|| (maxZoom >= rule.getZoomMax() && minZoom <= rule.getZoomMin())
			|| (maxZoom == -1 && minZoom == -1);
		// @formatter:on
		if (!fit) {
			return false;
		}
		if (closed && rule.getClosedType() == ClosedType.NO) {
			return false;
		}
		MatchingValues keys = rule.getKeys();
		MatchingValues values = rule.getValues();
		// first catch *=*
		if (keys.hasWildcard()) {
			if (values.hasWildcard()) {
				return true;
			}
			// if key = * and not value = *, dismiss
			return false;
		}
		// collect the entities tags that appear in the rule
		Set<String> vals = new HashSet<>();
		for (String key : keys.getValues()) {
			String val = tags.get(key);
			if (val != null) {
				// if value may have any value, jump out at the first key found
				if (values.hasWildcard()) {
					return true;
				}
				vals.add(val);
			}
		}
		// check each value
		for (String val : vals) {
			if (values.getValues().contains(val)) {
				return true;
			}
		}
		if (vals.size() == 0 && rule.isNegative()) {
			return true;
		}
		return false;
	}
}
