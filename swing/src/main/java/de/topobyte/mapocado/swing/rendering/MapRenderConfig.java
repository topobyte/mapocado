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

import java.util.List;
import java.util.Map;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.styles.ElementResolver;
import de.topobyte.mapocado.styles.MapStyleDataProvider;
import de.topobyte.mapocado.styles.classes.element.Caption;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.PathText;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.slim.CaptionSlim;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;
import de.topobyte.mapocado.styles.labels.elements.LabelContainer;
import de.topobyte.mapocado.styles.labels.elements.Rule;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class MapRenderConfig
{

	private ColorCode backgroundColor;
	private ColorCode mapOverlayInner;
	private ColorCode mapOverlayOuter;
	private ColorCode mapOverlayGpsInner;
	private ColorCode mapOverlayGpsOuter;
	private List<ObjectClass> classes;
	private List<Rule> labelRules;
	private Map<Rule, LabelContainer> labelStyles;
	private MapStyleDataProvider styleDataProvider;

	public MapRenderConfig(ColorCode backgroundColor, ColorCode mapOverlayInner,
			ColorCode mapOverlayOuter, ColorCode mapOverlayGpsInner,
			ColorCode mapOverlayGpsOuter, List<ObjectClass> classes,
			List<Rule> rules, Map<Rule, LabelContainer> styles,
			MapStyleDataProvider styleDataProvider)
	{
		this.backgroundColor = backgroundColor;
		this.mapOverlayInner = mapOverlayInner;
		this.mapOverlayOuter = mapOverlayOuter;
		this.mapOverlayGpsInner = mapOverlayGpsInner;
		this.mapOverlayGpsOuter = mapOverlayGpsOuter;
		this.classes = classes;
		this.labelRules = rules;
		this.labelStyles = styles;
		this.styleDataProvider = styleDataProvider;
	}

	public ColorCode getBackgroundColor()
	{
		return backgroundColor;
	}

	public ColorCode getOverlayInner()
	{
		return mapOverlayInner;
	}

	public ColorCode getOverlayOuter()
	{
		return mapOverlayOuter;
	}

	public ColorCode getOverlayGpsInner()
	{
		return mapOverlayGpsInner;
	}

	public ColorCode getOverlayGpsOuter()
	{
		return mapOverlayGpsOuter;
	}

	public List<ObjectClass> getClasses()
	{
		return classes;
	}

	public List<Rule> getLabelRules()
	{
		return labelRules;
	}

	public Map<Rule, LabelContainer> getLabelStyles()
	{
		return labelStyles;
	}

	public MapStyleDataProvider getStyleDataProvider()
	{
		return styleDataProvider;
	}

	public ElementResolver getResolver(StringPool poolForRefs)
	{
		return new ElementResolver(classes, poolForRefs);
	}

	public void createSlimClasses(StringPool poolForKeepKeys)
	{
		for (ObjectClass objectClass : classes) {
			RenderElement[] elements = objectClass.elements;
			for (int i = 0; i < elements.length; i++) {
				RenderElement element = elements[i];
				if (element instanceof PathText) {
					PathText p = (PathText) element;
					int keyId = poolForKeepKeys.getId(p.getKey());
					PathTextSlim slim = new PathTextSlim(p.getLevel(), keyId,
							p.getFontSize(), p.getStrokeWidth(), p.getFill(),
							p.getStroke(), p.getFontFamily(), p.getFontStyle());
					elements[i] = slim;
				} else if (element instanceof Caption) {
					Caption c = (Caption) element;
					int keyId = poolForKeepKeys.getId(c.getKey());
					CaptionSlim slim = new CaptionSlim(c.getLevel(), c.getTag(),
							keyId, c.hasDeltaY(), c.getDy(), c.getFontSize(),
							c.getStrokeWidth(), c.getFill(), c.getStroke(),
							c.getFontFamily(), c.getFontStyle());
					elements[i] = slim;
				}
			}
		}
	}

}