package de.topobyte.mapocado.mapformat.util;

import java.util.List;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;

import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;

public class ObjectClassLookup
{

	private TIntObjectHashMap<ObjectClassRef> lookup = new TIntObjectHashMap<>();

	public ObjectClassLookup(List<ObjectClassRef> objectClassRefs,
			StringPool refPool)
	{
		for (ObjectClassRef classRef : objectClassRefs) {
			String refName = classRef.getRef();
			int refId = refPool.getId(refName);
			lookup.put(refId, classRef);
		}
	}

	public ObjectClassRef get(int refId)
	{
		return lookup.get(refId);
	}

}
