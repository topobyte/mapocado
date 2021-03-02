package de.topobyte.mapocado.styles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.slimjars.dist.gnu.trove.procedure.TIntProcedure;

import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.model.Entity;
import de.topobyte.mapocado.mapformat.util.ArrayIntObjectMap;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.RenderElement;

public class ElementResolver
{

	private ArrayIntObjectMap<List<ObjectClass>> classMap = new ArrayIntObjectMap<>();

	public ElementResolver(List<ObjectClass> classes, StringPool poolForRefs)
	{
		// ensure that the classMap contains an entry for each class of the
		// mapfile
		for (int id = 0; id < poolForRefs.getNumberOfEntries(); id++) {
			classMap.put(id, new ArrayList<ObjectClass>());
		}
		// insert classes from the renderfile
		for (ObjectClass objectClass : classes) {
			String name = objectClass.getId();
			if (!poolForRefs.containsString(name)) {
				continue;
			}
			int id = poolForRefs.getId(name);
			List<ObjectClass> list = classMap.get(id);
			list.add(objectClass);
		}
	}

	public void lookupElements(Entity entity, int zoom,
			Collection<RenderElement> result)
	{
		filler.zoom = zoom;
		filler.result = result;
		entity.getClasses().forEach(filler);
	}

	private Filler filler = new Filler();

	private class Filler implements TIntProcedure
	{

		private int zoom = 0;
		private Collection<RenderElement> result = null;

		@Override
		public boolean execute(int ref)
		{
			List<ObjectClass> classes = classMap.get(ref);
			if (classes == null) {
				// no style for this class
				return true;
			}
			for (ObjectClass objectClass : classes) {
				if (zoom < objectClass.minZoom || zoom > objectClass.maxZoom) {
					continue;
				}

				// result.addAll(objectClass.elements);
				int size = objectClass.elements.length;
				for (int i = 0; i < size; i++) {
					result.add(objectClass.elements[i]);
				}
			}
			return true;
		}
	};
}
