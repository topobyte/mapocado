package de.topobyte.mapocado.mapformat.rtree.str;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.ITreeElement;
import de.topobyte.mapocado.mapformat.rtree.compat.IRTreeCompatible;

public class STRTreeBuilder<T> implements IRTreeCompatible<T>
{
	final static Logger logger = LoggerFactory.getLogger(STRTreeBuilder.class);

	private final int capacity;

	public STRTreeBuilder(int capacity)
	{
		this.capacity = capacity;
	}

	private List<STRConstructionElement<T>> elements = new ArrayList<>();

	@Override
	public void add(BoundingBox rect, T object)
	{
		STRConstructionElement<T> element = new STRConstructionElement<>(rect,
				object);
		elements.add(element);
	}

	@Override
	public IRTree<T> createIRTree() throws IOException
	{
		logger.debug("creating STR tree with elements: " + elements.size());
		return groupInit(elements);
	}

	private STRTree<T> groupInit(List<STRConstructionElement<T>> input)
	{
		int height = 1;
		if (input.size() <= capacity) {
			// just one leaf which is the root
			ITreeElement leaf = new STRLeaf<>(input);
			return new STRTree<>(leaf, height);
		}

		// we have at least a layer of leafs
		List<ITreeElement> leafs = new ArrayList<>();

		List<STRConstructionNode<T>> nodes = createNodes(elements, capacity);
		for (STRConstructionNode<T> constructionNode : nodes) {
			STRLeaf<T> leaf = new STRLeaf<>(constructionNode.getElements());
			leafs.add(leaf);
		}

		// now start recursion.
		List<ITreeElement> low = leafs;
		while (true) {
			height += 1;
			if (low.size() < capacity) {
				STRNode root = new STRNode(low);
				return new STRTree<>(root, height);
			}
			// create construction elements
			List<STRConstructionElement<ITreeElement>> elems = new ArrayList<>();
			for (ITreeElement elem : low) {
				elems.add(new STRConstructionElement<>(elem.getBoundingBox(),
						elem));
			}
			// group
			List<STRConstructionNode<ITreeElement>> highConstruct = createNodes(
					elems, capacity);
			// convert to nodes
			List<ITreeElement> high = new ArrayList<>();
			for (STRConstructionNode<ITreeElement> constructionNode : highConstruct) {
				List<STRConstructionElement<ITreeElement>> children = constructionNode
						.getElements();
				List<ITreeElement> childs = new ArrayList<>();
				for (STRConstructionElement<ITreeElement> element : children) {
					childs.add(element.getObject());
				}
				STRNode node = new STRNode(childs);
				high.add(node);
			}
			// continue on the next layer
			low = high;
		}
	}

	private static <V> List<STRConstructionNode<V>> createNodes(
			List<STRConstructionElement<V>> input, int capacity)
	{
		int numElems = input.size();
		logger.debug("numElems: " + numElems);
		int blocks = (int) Math.ceil(numElems / (double) capacity);
		logger.debug("blocks: " + blocks);
		// int elementsPerColumn = (int) Math.ceil(Math.sqrt(blocks) /
		// capacity);
		// logger.debug("elementsPerColumn: " + elementsPerColumn);
		// int xBlocks = (int) Math.ceil(numElems / elementsPerColumn);
		// logger.debug("xBlocks: " + xBlocks);
		int xBlocks = (int) Math.ceil(Math.sqrt(blocks));
		logger.debug("xBlocks: " + xBlocks);
		int elementsPerColumn = xBlocks * capacity;
		logger.debug("elementsPerColumn: " + elementsPerColumn);

		// sort along x axis
		Collections.sort(input, new STRConstructionXComparator<V>());

		// setup vertical slices
		List<List<STRConstructionElement<V>>> slices = new ArrayList<>();
		for (int i = 0; i < xBlocks; i++) {
			slices.add(new ArrayList<STRConstructionElement<V>>());
		}
		// split input into vertical slices
		for (int i = 0; i < input.size(); i++) {
			int bucket = i / elementsPerColumn;
			STRConstructionElement<V> constructionElement = input.get(i);
			slices.get(bucket).add(constructionElement);
		}

		// sort each slice along y axis
		for (int i = 0; i < xBlocks; i++) {
			List<STRConstructionElement<V>> slice = slices.get(i);
			Collections.sort(slice, new STRConstructionYComparator<V>());
		}

		List<STRConstructionNode<V>> nodes = new ArrayList<>();

		// split slices into nodes
		for (int i = 0; i < xBlocks; i++) {
			List<STRConstructionElement<V>> slice = slices.get(i);
			List<STRConstructionNode<V>> sliceNodes = createSliceNodes(slice,
					capacity);
			nodes.addAll(sliceNodes);
		}

		return nodes;
	}

	private static <V> List<STRConstructionNode<V>> createSliceNodes(
			List<STRConstructionElement<V>> slice, int capacity)
	{
		List<STRConstructionNode<V>> nodes = new ArrayList<>();

		int numElems = slice.size();
		int numNodes = (numElems + capacity - 1) / capacity;
		logger.debug("numElems: " + numElems);
		logger.debug("numNodes: " + numNodes);

		List<List<STRConstructionElement<V>>> leafElementLists = new ArrayList<>();
		for (int i = 0; i < numNodes; i++) {
			leafElementLists.add(new ArrayList<STRConstructionElement<V>>());
		}

		for (int i = 0; i < slice.size(); i++) {
			int bucket = i / capacity;
			leafElementLists.get(bucket).add(slice.get(i));
		}

		for (int i = 0; i < numNodes; i++) {
			List<STRConstructionElement<V>> leafElements = leafElementLists
					.get(i);
			STRConstructionNode<V> node = new STRConstructionNode<>(
					leafElements);
			nodes.add(node);
		}

		return nodes;
	}
}
