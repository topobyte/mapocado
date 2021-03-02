package de.topobyte.mapocado.styles.labels.elements;

public class Rule
{

	private int minZoom = 0;
	private int maxZoom = 0;
	private String key = null;

	public Rule()
	{
		// empty
	}

	public Rule(Rule rule)
	{
		minZoom = rule.minZoom;
		maxZoom = rule.maxZoom;
		key = rule.key;
	}

	public int getMinZoom()
	{
		return minZoom;
	}

	public void setMinZoom(int minZoom)
	{
		this.minZoom = minZoom;
	}

	public int getMaxZoom()
	{
		return maxZoom;
	}

	public void setMaxZoom(int maxZoom)
	{
		this.maxZoom = maxZoom;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

}
