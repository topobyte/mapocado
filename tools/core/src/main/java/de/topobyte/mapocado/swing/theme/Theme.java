package de.topobyte.mapocado.swing.theme;

public class Theme
{

	private final String name;
	private final String path;

	public Theme(String name, String path)
	{
		this.name = name;
		this.path = path;
	}

	public String getName()
	{
		return name;
	}

	public String getPath()
	{
		return path;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
