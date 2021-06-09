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

package de.topobyte.mapocado.mapformat.interval;

import java.util.ArrayList;
import java.util.List;

public class IntervalTreeTest
{

	public static void main(String[] args)
	{
		List<Integer> intervals = new ArrayList<>();
		intervals.add(3);
		intervals.add(7);
		intervals.add(13);

		List<String> values = new ArrayList<>();
		values.add("[-infinity..3[");
		values.add("[3..7[");
		values.add("[7..13[");
		values.add("[13..+infinity[");

		IntervalTree<Integer, String> tree = new NaiveIntervalTree<>(intervals,
				values);

		System.out.println("testing test for single interval");
		for (int i = -2; i < 20; i++) {
			String object = tree.getObject(i);
			System.out.println(String.format("%d: %s", i, object));
		}

		System.out.println("testing test for multiple intervals");
		for (int i = -2; i < 20; i++) {
			List<String> objects = tree.getObjects(i);
			System.out.println(String.format("%d: %s", i, objects));
		}
	}

}
