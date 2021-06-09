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

package de.topobyte.mapocado.rendering;

public class RenderingLogic
{

	private static final int STROKE_MIN_ZOOM_LEVEL = 12;
	private static final double STROKE_INCREASE = 1.5;

	private static final int DASH_MIN_ZOOM_LEVEL = 16;
	private static final double DASH_INCREASE = 1.5;

	private static final int FONT_MIN_ZOOM_LEVEL = 16;
	private static final double FONT_INCREASE = 1.3;

	private static final int CIRCLE_MIN_ZOOM_LEVEL = 12;
	private static final double CIRCLE_INCREASE = 1.5;

	public static float scaleStroke(float strokeWidth, int zoomLevel)
	{
		int zoomLevelDiff = zoomLevel - STROKE_MIN_ZOOM_LEVEL;
		if (zoomLevelDiff <= 0) {
			return strokeWidth;
		}
		return strokeWidth * (float) Math.pow(STROKE_INCREASE, zoomLevelDiff);
	}

	public static float scaleDash(float strokeWidth, int zoomLevel)
	{
		int zoomLevelDiff = zoomLevel - DASH_MIN_ZOOM_LEVEL;
		if (zoomLevelDiff <= 0) {
			return strokeWidth;
		}
		return strokeWidth * (float) Math.pow(DASH_INCREASE, zoomLevelDiff);
	}

	public static float scaleFont(float fontSize, int zoomLevel)
	{
		int zoomLevelDiff = zoomLevel - FONT_MIN_ZOOM_LEVEL;
		if (zoomLevelDiff <= 0) {
			return fontSize;
		}
		return fontSize * (float) Math.pow(FONT_INCREASE, zoomLevelDiff);
	}

	public static float scaleRadius(float radius, int zoomLevel)
	{
		int zoomLevelDiff = zoomLevel - CIRCLE_MIN_ZOOM_LEVEL;
		if (zoomLevelDiff <= 0) {
			return radius;
		}
		return radius * (float) Math.pow(CIRCLE_INCREASE, zoomLevelDiff);
	}

	public static float scale(float value, float factor)
	{
		return value * factor;
	}
}
