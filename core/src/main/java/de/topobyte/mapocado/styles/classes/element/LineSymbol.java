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

package de.topobyte.mapocado.styles.classes.element;

public class LineSymbol extends AbstractElement
{

	private static final long serialVersionUID = -8269968777911908695L;

	private final String source;
	private float offset;
	private boolean repeat;
	private float repeatDistance;

	public LineSymbol(int level, String source, float offset, boolean repeat,
			float repeatDistance)
	{
		super(level);
		this.source = source;
		this.offset = offset;
		this.repeat = repeat;
		this.repeatDistance = repeatDistance;
	}

	public String getSource()
	{
		return source;
	}

	public float getOffset()
	{
		return offset;
	}

	public boolean isRepeat()
	{
		return repeat;
	}

	public float getRepeatDistance()
	{
		return repeatDistance;
	}

}