package de.topobyte.jeography.viewer.nomioc;

import de.topobyte.nomioc.luqe.model.SqPoiType;

public class PoiTypeEntry
{

	private final Category category;
	private final SqPoiType type;

	public PoiTypeEntry(Category category, SqPoiType type)
	{
		this.category = category;
		this.type = type;
	}

	@Override
	public String toString()
	{
		if (category == Category.STREETS) {
			return "Streets";
		}
		if (type == null) {
			return "All pois";
		}
		return type.getName();
	}

	public Category getCategory()
	{
		return category;
	}

	public SqPoiType getType()
	{
		return type;
	}

}
