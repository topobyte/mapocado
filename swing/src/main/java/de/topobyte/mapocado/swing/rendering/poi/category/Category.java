package de.topobyte.mapocado.swing.rendering.poi.category;

public class Category
{
	private String name;
	private String preferenceKey;

	public Category(String name, String preferenceKey)
	{
		this.name = name;
		this.preferenceKey = preferenceKey;
	}

	public String getNameId()
	{
		return name;
	}

	public String getPreferenceKey()
	{
		return preferenceKey;
	}
}
