package de.topobyte.mapocado.mapformat.preprocess.classhistogram;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.geom.GeometryConverter;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.util.TagUtil;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.mapocado.styles.rules.match.RuleMatcher;
import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.access.OsmIteratorInput;
import de.topobyte.osm4j.core.access.ProgressMonitor;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;
import de.topobyte.osm4j.diskstorage.nodedb.NodeDB;
import de.topobyte.osm4j.geometry.GeometryBuilder;
import de.topobyte.osm4j.geometry.MissingEntitiesStrategy;
import de.topobyte.osm4j.geometry.MissingWayNodeStrategy;
import de.topobyte.osm4j.utils.OsmFileInput;

public class ClassHistogramBuilder
{

	static final Logger logger = LoggerFactory
			.getLogger(ClassHistogramBuilder.class);

	private RuleSet config;
	private OsmFileInput nodesFile, waysFile;
	private NodeDB nodeDB = null;

	private Multiset<String> counter = HashMultiset.create();

	public ClassHistogramBuilder(RuleSet config, OsmFileInput nodesFile,
			OsmFileInput waysFile, OsmFileInput relationsFile, NodeDB nodeDB)
	{
		this.config = config;
		this.nodesFile = nodesFile;
		this.waysFile = waysFile;
		this.nodeDB = nodeDB;
	}

	public void execute() throws IOException
	{
		RuleMatcher ruleMatcher = new RuleMatcher(config);

		StringPool refPool = Metadata
				.buildRefClassPool(config.getObjectClassRefs());

		StringPool keepPool = Metadata
				.buildKeepKeyPool(config.getObjectClassRefs());

		for (int i = 0; i < refPool.getNumberOfEntries(); i++) {
			logger.debug(String.format("adding #: %d, id: %s", i,
					refPool.getString(i)));
			counter.add(refPool.getString(i));
		}

		GeometryBuilder builder = new GeometryBuilder();
		builder.setMissingEntitiesStrategy(
				MissingEntitiesStrategy.BUILD_PARTIAL);
		builder.setMissingWayNodeStrategy(
				MissingWayNodeStrategy.SPLIT_POLYLINE);

		ProgressMonitor progressMonitor = new ProgressMonitor(
				"class histogram");

		OsmIteratorInput input;
		OsmIterator iterator;

		input = nodesFile.createIterator(true, false);
		iterator = input.getIterator();
		while (iterator.hasNext()) {
			EntityContainer container = iterator.next();
			if (container.getType() == EntityType.Node) {
				progressMonitor.nodeProcessed();
				OsmNode node = (OsmNode) container.getEntity();
				Map<String, String> tags = OsmModelUtil.getTagsAsMap(node);
				Map<Integer, String> itags = TagUtil.convertTags(tags,
						keepPool);

				Point point = builder.build(node);
				Coordinate coordinate = GeometryConverter.convert(point);

				Node mapNode = new Node(itags, coordinate);

				Set<ObjectClassRef> elements = ruleMatcher.getElements(mapNode,
						tags, -1, -1, true);

				put(elements);
			}
		}
		input.close();

		input = waysFile.createIterator(true, false);
		iterator = input.getIterator();
		while (iterator.hasNext()) {
			EntityContainer container = iterator.next();
			if (container.getType() == EntityType.Way) {
				progressMonitor.wayProcessed();
				OsmWay way = (OsmWay) container.getEntity();

				Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);

				Geometry geometry;
				try {
					geometry = builder.build(way, nodeDB);
				} catch (IllegalArgumentException | EntityNotFoundException e) {
					continue;
				}
				if (!geometry.isValid()) {
					continue;
				}
				if (!(geometry instanceof LineString)) {
					continue;
				}

				de.topobyte.mapocado.mapformat.geom.Linestring geom = GeometryConverter
						.convert((LineString) geometry);
				Map<Integer, String> itags = TagUtil.convertTags(tags,
						keepPool);
				Way mapWay = new Way(itags, geom);

				Set<ObjectClassRef> elements = ruleMatcher.getElements(mapWay,
						tags, -1, -1, true);

				put(elements);
			}
		}
		input.close();
	}

	private void put(Set<ObjectClassRef> elements)
	{
		if (elements.size() == 0) {
			return;
		}
		for (ObjectClassRef ref : elements) {
			String id = ref.getRef();
			counter.add(id);
		}
	}

	public StringPool createClassStringPool()
	{
		StringPool pool = new StringPool();
		Set<String> keys = Multisets.copyHighestCountFirst(counter)
				.elementSet();
		for (String key : keys) {
			int occs = counter.count(key);
			logger.debug(String.format("adding %s: %d", key, occs));
			pool.add(key);
		}
		return pool;
	}

}
