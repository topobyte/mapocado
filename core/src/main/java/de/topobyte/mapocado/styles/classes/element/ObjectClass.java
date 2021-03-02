package de.topobyte.mapocado.styles.classes.element;

public class ObjectClass
{

	public final String id;
	public final int minZoom;
	public final int maxZoom;
	public RenderElement[] elements;

	public ObjectClass(String id, RenderElement[] elements, int minZoom,
			int maxZoom)
	{
		this.id = id;
		this.elements = elements;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	public String getId()
	{
		return id;
	}

	public int getMinZoom()
	{
		return minZoom;
	}

	public int getMaxZoom()
	{
		return maxZoom;
	}
}
