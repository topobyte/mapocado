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

package de.topobyte.mapocado.swing.rendering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.slimjars.dist.gnu.trove.map.hash.THashMap;

import de.topobyte.mapocado.styles.classes.element.RenderElement;

public class RuleShapeMap<T, V>
{

	// private Map<T, List<V>> ruleToElements = new HashMap<T, List<V>>();
	private THashMap<T, List<V>> ruleToElements = new THashMap<>();

	public void put(T rule, V container)
	{
		/*
		 * TODO: replace hashing with constant lookup table. This is actually a
		 * little complicated and does not have a high priority currently. It
		 * could be done like in the android version, but that makes things the
		 * design a little more complicated.
		 */
		List<V> list = ruleToElements.get(rule);
		if (list == null) {
			list = new ArrayList<>();
			ruleToElements.put(rule, list);
		}
		list.add(container);
	}

	public THashMap<T, List<V>> getRuleToElements()
	{
		return ruleToElements;
	}

	public void clear()
	{
		ruleToElements.clear();
	}

	public static <A, B extends A, C extends A, D extends A> Map<RenderElement, List<A>> merge(
			Map<RenderElement, List<B>> ruleToWays,
			Map<RenderElement, List<C>> ruleToRelations,
			Map<RenderElement, List<D>> ruleToNodes)
	{
		THashMap<RenderElement, List<A>> result = new THashMap<>();

		copy(result, ruleToWays);
		copySecure(result, ruleToRelations);
		copySecure(result, ruleToNodes);

		return result;
	}

	private static <A, B extends A> void copy(
			THashMap<RenderElement, List<A>> result,
			Map<RenderElement, List<B>> ruleToShape)
	{
		for (RenderElement element : ruleToShape.keySet()) {
			List<A> items = new ArrayList<>();
			items.addAll(ruleToShape.get(element));
			result.put(element, items);
		}
	}

	private static <A, B extends A> void copySecure(
			THashMap<RenderElement, List<A>> result,
			Map<RenderElement, List<B>> ruleToShape)
	{
		for (RenderElement element : ruleToShape.keySet()) {
			List<A> items = result.get(element);
			if (items == null) {
				items = new ArrayList<>();
				result.put(element, items);
			}
			items.addAll(ruleToShape.get(element));
			result.put(element, items);
		}
	}

}
