package de.topobyte.mapocado.android.rendering.gather;

import java.util.ArrayList;
import java.util.List;

import com.slimjars.dist.gnu.trove.map.hash.TIntObjectHashMap;
import com.slimjars.dist.gnu.trove.procedure.TIntObjectProcedure;
import com.slimjars.dist.gnu.trove.procedure.TIntProcedure;

import de.topobyte.mapocado.android.style.RenderClassHelper;
import de.topobyte.mapocado.android.style.RenderElementGatherer;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.rtree.disk.ElementCallback;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;

public class NodeGatherer implements ElementCallback<Node>
{

	private RenderElementGatherer renderGatherer;
	private RenderClassHelper renderClassHelper;
	private Filler filler = new Filler();
	private Node node;
	private int zoom;

	private List<NodeData> objects = new ArrayList<>();
	// This used to be a boolean field. It led to VerifyErrors on the HTC G1,
	// apparently some issues that occur due to dalvik stuff that is broken on
	// old versions of android (1.6?)
	private int hasSymbol = 0;

	public NodeGatherer(int zoom, RenderClassHelper renderClassHelper,
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
		objects.clear();
		hasSymbol = 0;
		node.getClasses().forEach(filler);
		for (NodeData object : objects) {
			object.setHasSymbol(hasSymbol != 0);
		}
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

				TIntObjectHashMap<String> tags = node.getTags();
				TIntObjectHashMap<String> tagsClone = clone(tags);
				if (renderClassHelper.hasSymbol(ref)) {
					hasSymbol += 1;
				}
				NodeData nodeData = new NodeData(node.getPoint(), false,
						tagsClone);
				objects.add(nodeData);

				int size = objectClass.elements.length;
				for (int i = 0; i < size; i++) {
					int[] ids = renderClassHelper
							.getRenderElementIds(objectClassId);
					for (int id : ids) {
						renderGatherer.addNode(id, nodeData);
					}
				}
			}
			return true;
		}

		private TIntObjectHashMap<String> clone(TIntObjectHashMap<String> tags)
		{
			// NOTE: this one does not work, will fail early versions of android
			// return new TIntObjectHashMap<String>(tags);

			final TIntObjectHashMap<String> copy = new TIntObjectHashMap<>();
			tags.forEachEntry(new TIntObjectProcedure<String>() {

				@Override
				public boolean execute(int key, String value)
				{
					copy.put(key, value);
					return true;
				}

			});
			return copy;
		}
	};

}
