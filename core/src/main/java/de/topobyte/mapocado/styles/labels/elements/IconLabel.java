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

package de.topobyte.mapocado.styles.labels.elements;

public class IconLabel extends PlainLabel
{

	private String image = null;
	private float height = 14;

	public IconLabel()
	{
		super();
	}

	public IconLabel(PlainLabel label)
	{
		super(label);
	}

	public IconLabel(IconLabel label)
	{
		this((PlainLabel) label);
		image = label.image;
		height = label.height;
	}

	public String getImage()
	{
		return image;
	}

	public void setImage(String image)
	{
		this.image = image;
	}

	public float getIconHeight()
	{
		return height;
	}

	public void setIconHeight(float height)
	{
		this.height = height;
	}

}
