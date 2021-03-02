package de.topobyte.mapocado.swing.rendering.poi.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapfileCategory extends Category
{

	private List<String> ids = new ArrayList<>();

	public MapfileCategory(String name, String preferenceKey,
			String... identifiers)
	{
		super(name, preferenceKey);
		Collections.addAll(ids, identifiers);
	}

}
