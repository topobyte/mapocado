package de.topobyte.mapocado.android.style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import de.topobyte.collections.util.ListUtil;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.util.ArrayIntObjectMap;
import de.topobyte.mapocado.styles.classes.ElementType;
import de.topobyte.mapocado.styles.classes.element.Area;
import de.topobyte.mapocado.styles.classes.element.BvgLineSymbol;
import de.topobyte.mapocado.styles.classes.element.BvgSymbol;
import de.topobyte.mapocado.styles.classes.element.Caption;
import de.topobyte.mapocado.styles.classes.element.Circle;
import de.topobyte.mapocado.styles.classes.element.Line;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.PathText;
import de.topobyte.mapocado.styles.classes.element.PngLineSymbol;
import de.topobyte.mapocado.styles.classes.element.PngSymbol;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.RenderElementComparable;
import de.topobyte.mapocado.styles.classes.element.slim.CaptionSlim;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;

public class RenderClassHelper
{

	// <ref> to ObjectClass ids
	private ArrayIntObjectMap<int[]> classMap = new ArrayIntObjectMap<>();
	// <ref> to boolean whether one of the reachable render elements is a symbol
	public boolean[] hasSymbolElement;

	// maps ObjectClass ids to ObjectClass instances
	private ObjectClass[] objectClassMap;

	// maps (ObjectClass.id, #num) to renderElementIds
	public int[][] renderElementIds;

	// maps renderElementIds to RenderElement instances
	public RenderElement[] renderElements;
	// stores the element classes
	public ElementType[] renderElementTypes;
	// store whether a RenderElement

	// defines the iteration order of renderElementIds
	public int[] zOrderedRenderElementIds;
	// the ids of textPathElement instances
	public int[] textPathElementIds;
	// the ids of symbol instances
	public int[] bvgSymbolElementIds;
	public int[] pngSymbolElementIds;
	public int[] captionElementIds;

	public RenderClassHelper(List<ObjectClass> objectClasses,
			StringPool poolForRefs)
	{
		ArrayIntObjectMap<List<Integer>> dynamicClassMap = new ArrayIntObjectMap<>();

		objectClassMap = new ObjectClass[objectClasses.size()];

		renderElementIds = new int[objectClasses.size()][];
		List<RenderElement> renderElementList = new ArrayList<>();

		hasSymbolElement = new boolean[poolForRefs.getNumberOfEntries()];
		for (int id = 0; id < poolForRefs.getNumberOfEntries(); id++) {
			dynamicClassMap.put(id, new ArrayList<Integer>());
			hasSymbolElement[id] = false;
		}

		// register render elements here
		Map<RenderElement, Integer> elementToId = new HashMap<>();

		int objectClassId = 0;
		int renderElementId = 0;
		for (ObjectClass objectClass : objectClasses) {
			// assign an id to this object class
			int myObjectClassId = objectClassId++;
			objectClassMap[myObjectClassId] = objectClass;
			String name = objectClass.getId();
			// Subtle bug here, when not checking for containmente: getId(name)
			// returns 0 for non-contained classes, so this results in those
			// render elements being mapped to the object class with id = 0
			if (!poolForRefs.containsString(name)) {
				continue;
			}
			int id = poolForRefs.getId(name);
			// add it to the map from rule class to object classes
			List<Integer> list = dynamicClassMap.get(id);
			list.add(myObjectClassId);

			RenderElement[] elements = objectClass.elements;
			renderElementIds[myObjectClassId] = new int[elements.length];
			for (int k = 0; k < renderElementIds[myObjectClassId].length; k++) {
				int renderId;
				if (elementToId.containsKey(elements[k])) {
					renderId = elementToId.get(elements[k]);
				} else {
					renderId = renderElementId++;
					elementToId.put(elements[k], renderId);
					renderElementList.add(elements[k]);
				}
				renderElementIds[myObjectClassId][k] = renderId;
			}

			for (RenderElement element : elements) {
				if (element instanceof BvgSymbol
						|| element instanceof PngSymbol) {
					hasSymbolElement[id] |= true;
				}
			}
		}

		// create static class map
		for (int id = 0; id < poolForRefs.getNumberOfEntries(); id++) {
			List<Integer> classIds = dynamicClassMap.get(id);
			classMap.put(id, ListUtil.asIntArray(classIds));
		}

		// create element lookup
		renderElements = new RenderElement[renderElementList.size()];
		renderElementTypes = new ElementType[renderElementList.size()];
		for (int i = 0; i < renderElementList.size(); i++) {
			RenderElement element = renderElementList.get(i);
			renderElements[i] = element;
			renderElementTypes[i] = getRenderElementType(element);
		}

		// create z-ordered list
		zOrderedRenderElementIds = new int[renderElementList.size()];
		Collections.sort(renderElementList, new RenderElementComparable());
		for (int i = 0; i < renderElementList.size(); i++) {
			RenderElement element = renderElementList.get(i);
			int id = elementToId.get(element);
			zOrderedRenderElementIds[i] = id;
		}

		// extract specific elements
		List<RenderElement> pathTextList = new ArrayList<>();
		List<RenderElement> bvgSymbolList = new ArrayList<>();
		List<RenderElement> pngSymbolList = new ArrayList<>();
		List<RenderElement> captionList = new ArrayList<>();
		for (int i = 0; i < renderElements.length; i++) {
			RenderElement element = renderElements[i];
			ElementType elementType = renderElementTypes[i];
			switch (elementType) {
			case PATHTEXT:
				pathTextList.add(element);
				break;
			case BVG_SYMBOL:
				bvgSymbolList.add(element);
				break;
			case PNG_SYMBOL:
				pngSymbolList.add(element);
				break;
			case CAPTION:
				captionList.add(element);
				break;
			default:
				break;
			}
		}
		Log.i("renderclasses",
				"number of textPath elements: " + pathTextList.size());
		Log.i("renderclasses",
				"number of png symbol elements: " + pngSymbolList.size());
		Log.i("renderclasses",
				"number of bvg symbol elements: " + bvgSymbolList.size());
		Log.i("renderclasses",
				"number of caption elements: " + captionList.size());

		textPathElementIds = toIdArray(pathTextList, elementToId);
		bvgSymbolElementIds = toIdArray(bvgSymbolList, elementToId);
		pngSymbolElementIds = toIdArray(pngSymbolList, elementToId);
		captionElementIds = toIdArray(captionList, elementToId);
	}

	private int[] toIdArray(List<RenderElement> elements,
			Map<RenderElement, Integer> elementToId)
	{
		int[] elementIds = new int[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			RenderElement pathTextElement = elements.get(i);
			int id = elementToId.get(pathTextElement);
			elementIds[i] = id;
		}
		return elementIds;
	}

	private ElementType getRenderElementType(RenderElement element)
	{
		if (element instanceof Line) {
			return ElementType.LINE;
		} else if (element instanceof Area) {
			return ElementType.AREA;
		} else if (element instanceof Circle) {
			return ElementType.CIRCLE;
		} else if (element instanceof Caption) {
			return ElementType.CAPTION;
		} else if (element instanceof CaptionSlim) {
			return ElementType.CAPTION;
		} else if (element instanceof BvgSymbol) {
			return ElementType.BVG_SYMBOL;
		} else if (element instanceof PathText) {
			return ElementType.PATHTEXT;
		} else if (element instanceof PathTextSlim) {
			return ElementType.PATHTEXT;
		} else if (element instanceof BvgLineSymbol) {
			return ElementType.BVG_LINESYMBOL;
		} else if (element instanceof PngSymbol) {
			return ElementType.PNG_SYMBOL;
		} else if (element instanceof PngLineSymbol) {
			return ElementType.PNG_LINESYMBOL;
		}
		return null;
	}

	public ObjectClass getObjectClass(int id)
	{
		return objectClassMap[id];
	}

	public int[] getObjectClassIds(int ref)
	{
		return classMap.get(ref);
	}

	public int[] getRenderElementIds(int objectClassId)
	{
		return renderElementIds[objectClassId];
	}

	public boolean hasSymbol(int ref)
	{
		return hasSymbolElement[ref];
	}

}
