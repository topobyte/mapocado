package de.topobyte.jeography.tools.cityviewer.theme;

public class Style
{
	private String name;
	private String file;

	public Style(String name, String file)
	{
		this.name = name;
		this.file = file;
	}

	public String getName()
	{
		return name;
	}

	public String getFile()
	{
		return file;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
