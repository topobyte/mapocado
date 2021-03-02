package de.topobyte.mapocado.android.rendering.gather;

import com.slimjars.dist.gnu.trove.procedure.TIntProcedure;

import de.topobyte.mapocado.android.style.RenderClassHelper;
import de.topobyte.mapocado.android.style.RenderElementGatherer;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.rtree.disk.ElementCallback;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;

public class CoordinateGatherer implements ElementCallback<Node>
{

	private RenderElementGatherer renderGatherer;
	private RenderClassHelper renderClassHelper;
	private Filler filler = new Filler();
	private Node node;
	private int zoom;

	public CoordinateGatherer(int zoom, RenderClassHelper renderClassHelper,
			RenderElementGatherer renderGatherer)
	{
		this.renderClassHelper = renderClassHelper;
		this.renderGatherer = renderGatherer;
		this.zoom = zoom;
	}

	@Override
	public void handle(Node node)
	{
		this.node = node;
		node.getClasses().forEach(filler);
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
				// TODO: at all places where elements.length is used for
				// gathering, the usage is actually redundant and probably
				// wrong, because i is never used and we go over id:ids later.
				int size = objectClass.elements.length;
				for (int i = 0; i < size; i++) {
					int[] ids = renderClassHelper
							.getRenderElementIds(objectClassId);
					for (int id : ids) {
						renderGatherer.addCoordinate(id, node.getPoint());
					}
				}
			}
			return true;
		}
	};

}
