package de.topobyte.mapocado.swing.rendering.poi;

import java.util.ArrayList;
import java.util.List;

import de.topobyte.mapocado.swing.rendering.poi.category.Category;

public class Group
{

	private String title;
	private List<Category> children = new ArrayList<>();

	public Group(String title)
	{
		this.title = title;
	}

	public String getTitleId()
	{
		return title;
	}

	public void add(Category category)
	{
		children.add(category);
	}

	public List<Category> getChildren()
	{
		return children;
	}

}
