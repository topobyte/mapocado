package de.topobyte.mapocado.mapformat.rtree.disk.cache;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.rtree.disk.Node;

/**
 * A cache implementation that stored everything in memory and never releases
 * any resources.
 * 
 * @author Sebastian Kürten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class NaiveInnerNodeCache implements InnerNodeCache
{

	TIntObjectHashMap<Node> map = new TIntObjectHashMap<>();

	@Override
	public Node get(int address, int depth)
	{
		return map.get(address);
	}

	@Override
	public void put(int address, int depth, Node node)
	{
		map.put(address, node);
	}

}
