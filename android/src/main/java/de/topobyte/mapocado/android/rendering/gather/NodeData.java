package de.topobyte.mapocado.android.rendering.gather;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.geom.Coordinate;

public class NodeData
{

	private final Coordinate coordinate;
	private boolean hasSymbol;
	private TIntObjectHashMap<String> tags = new TIntObjectHashMap<>();

	public NodeData(Coordinate coordinate, boolean hasSymbol,
			TIntObjectHashMap<String> tags)
	{
		this.coordinate = coordinate;
		this.hasSymbol = hasSymbol;
		this.tags = tags;
	}

	public Coordinate getPoint()
	{
		return coordinate;
	}

	public boolean hasSymbol()
	{
		return hasSymbol;
	}

	public void setHasSymbol(boolean hasSymbol)
	{
		this.hasSymbol = hasSymbol;
	}

	public TIntObjectHashMap<String> getTags()
	{
		return tags;
	}

}
