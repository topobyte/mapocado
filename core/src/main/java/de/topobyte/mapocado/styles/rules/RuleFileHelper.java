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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;

public class RuleFileHelper
{

	public static Map<String, List<List<Rule>>> buildClassToRules(
			List<Rule> rules)
	{
		RuleFileHelper helper = new RuleFileHelper();
		for (Rule rule : rules) {
			helper.handle(rule);
		}
		return helper.map;
	}

	private Stack<Rule> stack = new Stack<>();
	private Map<String, List<List<Rule>>> map = new HashMap<>();

	private void handle(Rule rule)
	{
		stack.push(rule);
		handleCurrent();
		for (Rule subrule : rule.getRules()) {
			handle(subrule);
		}
		stack.pop();
	}

	private void handleCurrent()
	{
		Rule rule = stack.peek();
		List<Rule> rules = new ArrayList<>();
		rules.addAll(stack);
		List<ObjectClassRef> classes = rule.getClasses();
		for (ObjectClassRef classRef : classes) {
			String ref = classRef.getRef();
			List<List<Rule>> list = map.get(ref);
			if (list == null) {
				list = new ArrayList<>();
				map.put(ref, list);
			}
			list.add(rules);
		}
	}

}
