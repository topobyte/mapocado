package de.topobyte.mapocado.swing.rendering.poi.category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseCategory extends Category
{

	private List<String> ids = new ArrayList<>();

	public DatabaseCategory(String name, String preferenceKey,
			String... identifiers)
	{
		super(name, preferenceKey);
		Collections.addAll(ids, identifiers);
	}

	public List<String> getIdentifiers()
	{
		return ids;
	}

}
