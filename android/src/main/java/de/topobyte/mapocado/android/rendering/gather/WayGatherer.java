package de.topobyte.mapocado.android.rendering.gather;

import com.slimjars.dist.gnu.trove.procedure.TIntProcedure;

import de.topobyte.mapocado.android.style.RenderClassHelper;
import de.topobyte.mapocado.android.style.RenderElementGatherer;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.rtree.disk.ElementCallback;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.RenderElement;
import de.topobyte.mapocado.styles.classes.element.slim.PathTextSlim;

public class WayGatherer implements ElementCallback<Way>
{

	private RenderClassHelper renderClassHelper;
	private Filler filler = new Filler();
	private RenderElementGatherer renderGatherer;
	private Way way;
	private int zoom;

	public WayGatherer(int zoom, RenderClassHelper renderClassHelper,
			RenderElementGatherer renderGatherer)
	{
		this.renderClassHelper = renderClassHelper;
		this.renderGatherer = renderGatherer;
		this.zoom = zoom;
	}

	@Override
	public void handle(Way way)
	{
		this.way = way;
		way.getClasses().forEach(filler);
	}

	private class Filler implements TIntProcedure
	{

		@Override
		public boolean execute(int ref)
		{
			int[] classIds = renderClassHelper.getObjectClassIds(ref);
			for (int objectClassId : classIds) {
				ObjectClass objectClass = renderClassHelper
						.getObjectClass(objectClassId);
				if (zoom < objectClass.minZoom || zoom > objectClass.maxZoom) {
					continue;
				}

				int size = objectClass.elements.length;
				for (int i = 0; i < size; i++) {
					int[] ids = renderClassHelper
							.getRenderElementIds(objectClassId);
					for (int id : ids) {
						RenderElement element = renderClassHelper.renderElements[id];
						if (element instanceof PathTextSlim) {
							PathTextSlim pathText = (PathTextSlim) element;
							int keyId = pathText.getValK();
							String text = way.getTags().get(keyId);
							PathTextElement path = new PathTextElement(
									way.getString(), text);
							renderGatherer.addPathText(id, path);
						} else {
							renderGatherer.addWay(id, way.getString());
						}
					}
				}
			}
			return true;
		}
	};
}
