package de.topobyte.mapocado.styles.classes.element;

import java.util.Collection;

import com.slimjars.dist.gnu.trove.set.hash.TIntHashSet;

import de.topobyte.mapocado.mapformat.io.StringPool;

public class ElementHelper
{

	public static int[] getReferenceIds(Collection<ObjectClassRef> heavyRefs,
			StringPool refPool)
	{
		TIntHashSet ids = new TIntHashSet();
		for (ObjectClassRef heavy : heavyRefs) {
			int refId = refPool.getId(heavy.getRef());
			ids.add(refId);
		}
		return ids.toArray();
	}
}
