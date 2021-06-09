// Copyright 2021 Sebastian Kuerten
//
// This file is part of mapocado.
//
// mapocado is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// mapocado is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with mapocado. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.mapocado.mapformat.creation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.jts.utils.polygons.split.PolygonSplitUtil;
import de.topobyte.jts.utils.polygons.split.SplitMode;
import de.topobyte.mapocado.mapformat.SegmentationHelper;
import de.topobyte.mapocado.mapformat.geom.Coordinate;
import de.topobyte.mapocado.mapformat.geom.GeometryConverter;
import de.topobyte.mapocado.mapformat.geom.Multipolygon;
import de.topobyte.mapocado.mapformat.interval.IntervalArray;
import de.topobyte.mapocado.mapformat.interval.IntervalTree;
import de.topobyte.mapocado.mapformat.interval.NaiveIntervalTree;
import de.topobyte.mapocado.mapformat.io.FilePartition;
import de.topobyte.mapocado.mapformat.io.Header;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.io.StartPointCalculator;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.model.Node;
import de.topobyte.mapocado.mapformat.model.Relation;
import de.topobyte.mapocado.mapformat.model.TextNode;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.preprocess.classhistogram.ClassHistogramBuilder;
import de.topobyte.mapocado.mapformat.rtree.BoundingBox;
import de.topobyte.mapocado.mapformat.rtree.IRTree;
import de.topobyte.mapocado.mapformat.rtree.compat.IRTreeCompatible;
import de.topobyte.mapocado.mapformat.rtree.disk.GeneralDiskTreeCreator;
import de.topobyte.mapocado.mapformat.rtree.disk.PointDiskTreeCreator;
import de.topobyte.mapocado.mapformat.rtree.str.STRTreeBuilder;
import de.topobyte.mapocado.mapformat.util.ObjectClassLookup;
import de.topobyte.mapocado.mapformat.util.TagUtil;
import de.topobyte.mapocado.styles.classes.element.ElementHelper;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.mapocado.styles.rules.match.RuleMatcher;
import de.topobyte.osm4j.core.model.iface.OsmNode;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.iface.OsmWay;
import de.topobyte.osm4j.diskstorage.nodedb.NodeDB;
import de.topobyte.osm4j.geometry.OsmEntityGeometryHandler;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.simplemapfile.core.EntityFile;
import de.topobyte.simplemapfile.xml.SmxFileWriter;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 */
public class MapFormatCreate implements OsmEntityGeometryHandler
{

	static final Logger logger = LoggerFactory.getLogger(MapFormatCreate.class);

	private File outputFile;
	private RuleSet config;
	private OsmFileInput nodesFile, waysFile, relationsFile;
	private NodeDB nodeDB = null;

	private IntervalArray intervalsNodes;
	private IntervalArray intervalsWays;
	private IntervalArray intervalsRelations;
	private IntervalTree<Integer, IRTreeCompatible<Node>> intervalTreeNodes;
	private IntervalTree<Integer, IRTreeCompatible<Way>> intervalTreeWays;
	private IntervalTree<Integer, IRTreeCompatible<Relation>> intervalTreeRelations;
	private List<IRTreeCompatible<Node>> treesNodes;
	private List<IRTreeCompatible<Way>> treesWays;
	private List<IRTreeCompatible<Relation>> treesRelations;
	private IRTreeCompatible<TextNode> treeHousenumbers;

	private StringPool refPool;
	private StringPool keepPool;
	private ObjectClassLookup classLookup;
	private RuleMatcher ruleMatcher;
	private StartPointCalculator calculator;

	private Geometry boundary;
	private File logsDir;
	private Geometry landGeometry = null;

	private final Integer[] limitsNodes;
	private final Integer[] limitsWays;
	private final Integer[] limitsRelations;

	private int pointLimit = 1000;

	public MapFormatCreate(File outputFile, RuleSet config,
			OsmFileInput nodesFile, OsmFileInput waysFile,
			OsmFileInput relationsFile, NodeDB nodeDB, Geometry boundary,
			File logsDir, Geometry landGeometry, List<Integer> limitsNodes,
			List<Integer> limitsWays, List<Integer> limitsRelations)
	{
		this.outputFile = outputFile;
		this.config = config;
		this.nodesFile = nodesFile;
		this.waysFile = waysFile;
		this.relationsFile = relationsFile;
		this.nodeDB = nodeDB;
		this.boundary = boundary;
		this.logsDir = logsDir;
		this.landGeometry = landGeometry;
		this.limitsNodes = limitsNodes.toArray(new Integer[0]);
		this.limitsWays = limitsWays.toArray(new Integer[0]);
		this.limitsRelations = limitsRelations.toArray(new Integer[0]);
	}

	public void prepare() throws IOException
	{
		intervalsNodes = new IntervalArray(limitsNodes);
		intervalsWays = new IntervalArray(limitsWays);
		intervalsRelations = new IntervalArray(limitsRelations);

		treesNodes = createTrees(intervalsNodes);
		treesWays = createTrees(intervalsWays);
		treesRelations = createTrees(intervalsRelations);

		treeHousenumbers = new STRTreeBuilder<>(16);

		intervalTreeNodes = new NaiveIntervalTree<>(intervalsNodes, treesNodes);
		intervalTreeWays = new NaiveIntervalTree<>(intervalsWays, treesWays);
		intervalTreeRelations = new NaiveIntervalTree<>(intervalsRelations,
				treesRelations);

		// StringPool refPool = Metadata.buildRefClassPool(config
		// .getObjectClassRefs());

		ClassHistogramBuilder classHistogramBuilder = new ClassHistogramBuilder(
				config, nodesFile, waysFile, relationsFile, nodeDB);
		classHistogramBuilder.execute();
		refPool = classHistogramBuilder.createClassStringPool();

		keepPool = Metadata.buildKeepKeyPool(config.getObjectClassRefs());

		classLookup = new ObjectClassLookup(config.getObjectClassRefs(),
				refPool);

		ruleMatcher = new RuleMatcher(config);

		calculator = new StartPointCalculator();

		if (logsDir != null) {
			if (!logsDir.exists()) {
				logsDir.mkdirs();
			}
			if (!logsDir.exists() && logsDir.canWrite()) {
				logger.error("unable to create the dump for failed polygons");
				System.exit(1);
			}
		}

		if (landGeometry != null) {
			// compute sea
			Geometry sea = boundary.difference(landGeometry);
			Multipolygon mercPolygon = null;
			if (sea instanceof MultiPolygon) {
				mercPolygon = GeometryConverter.convert((MultiPolygon) sea);
			} else if (sea instanceof Polygon) {
				de.topobyte.mapocado.mapformat.geom.Polygon polygon = GeometryConverter
						.convert((Polygon) sea);
				mercPolygon = new Multipolygon(
						new de.topobyte.mapocado.mapformat.geom.Polygon[] {
								polygon });
			} else {
				logger.warn("computation of sea resulted in a: "
						+ sea.getGeometryType());
			}
			if (mercPolygon != null) {
				Map<String, String> tags = new HashMap<>();
				tags.put("natural", "fakedsea");
				processMultipolygon(mercPolygon, sea, tags, null);
			}
		}
	}

	public void createFile()
	{
		// create file

		Header header = new Header();
		FilePartition filePartition = new FilePartition();

		Metadata metadata = new Metadata(intervalsNodes, intervalsWays,
				intervalsRelations, filePartition, refPool, keepPool);

		int numPartitions = treesNodes.size() + treesWays.size()
				+ treesRelations.size() + 1;
		for (int k = 0; k < numPartitions; k++) {
			filePartition.add(0);
		}

		metadata.setStart(calculator.getPoint());

		try {
			RandomAccessFile file = new RandomAccessFile(outputFile, "rw");
			int position = 0;

			int headerPosition = position;
			int headerSize = header.write(file, headerPosition);
			position += headerSize;

			int metadataPosition = position;
			int metadataSize = metadata.write(file, metadataPosition);
			logger.info("metadata size: " + metadataSize);
			position += metadataSize;

			filePartition = new FilePartition();
			metadata.setFilePartition(filePartition);

			for (IRTreeCompatible<Way> tree : treesWays) {
				filePartition.add(position);
				IRTree<Way> ramTree = tree.createIRTree();
				int size = GeneralDiskTreeCreator.create(file, position,
						ramTree, metadata, null);
				position += size;
				logger.info("way tree size: " + size);
			}
			for (IRTreeCompatible<Relation> tree : treesRelations) {
				filePartition.add(position);
				IRTree<Relation> ramTree = tree.createIRTree();
				int size = GeneralDiskTreeCreator.create(file, position,
						ramTree, metadata, null);
				position += size;
				logger.info("relation tree size: " + size);
			}
			for (IRTreeCompatible<Node> tree : treesNodes) {
				filePartition.add(position);
				IRTree<Node> ramTree = tree.createIRTree();
				int size = PointDiskTreeCreator.create(file, position, ramTree,
						metadata);
				position += size;
				logger.info("node tree size: " + size);
			}

			filePartition.add(position);
			IRTree<TextNode> ramHousenumbers = treeHousenumbers.createIRTree();
			int size = PointDiskTreeCreator.create(file, position,
					ramHousenumbers, metadata);
			position += size;
			logger.info("housenumber tree size: " + size);

			int metadataSizeSecondPass = metadata.write(file, metadataPosition);

			if (metadataSize != metadataSizeSecondPass) {
				logger.error("second pass metadata has different size.");
			}

			int fileLength = position;
			logger.debug("file size: " + fileLength);
			header.setFileLength(fileLength);

			byte[] checksum = createChecksum(file, 0, fileLength);
			header.setChecksum(checksum);
			logger.debug("checksum: " + header.getReadableChecksum());

			int headerSizeSecondPass = header.write(file, headerPosition);
			if (headerSize != headerSizeSecondPass) {
				logger.error("second pass header has different size.");
			}

			file.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte[] createChecksum(RandomAccessFile file, int position,
			int fileLength) throws NoSuchAlgorithmException, IOException
	{
		byte[] buffer = new byte[1024];
		MessageDigest digest = MessageDigest.getInstance("MD5");

		for (int i = position; i < fileLength; i += buffer.length) {
			file.seek(i);
			int read = file.read(buffer, 0, buffer.length);
			digest.update(buffer, 0, read);
		}

		byte[] checksum = digest.digest();
		return checksum;
	}

	private static <T> List<IRTreeCompatible<T>> createTrees(
			List<Integer> intervals)
	{
		List<IRTreeCompatible<T>> trees = new ArrayList<>();
		for (int i = 0; i <= intervals.size(); i++) {
			// trees.add(new JsiIRTreeCompatible<T>(10, 20));
			trees.add(new STRTreeBuilder<T>(16));
		}
		return trees;
	}

	@Override
	public void processNode(OsmNode node, Point point, Map<String, String> tags)
	{
		Map<Integer, String> itags = TagUtil.convertTags(tags, keepPool);

		Coordinate coordinate = GeometryConverter.convert(point);

		Node mapNode = new Node(itags, coordinate);
		Set<ObjectClassRef> elements = ruleMatcher.getElements(mapNode, tags,
				-1, -1, true);

		if (isHousenumber(tags)) {
			housenumber(point, tags);
		}

		if (elements.size() == 0) {
			return;
		}
		// Set<LightObjectClassRef> lightRefs = ElementHelper
		// .createLightReferences(elements, refPool);
		// mapNode.getClasses().addAll(lightRefs);
		mapNode.getClasses()
				.add(ElementHelper.getReferenceIds(elements, refPool));
		mapNode.eraseUnnecessaryTags(elements, keepPool);

		BoundingBox rect = new BoundingBox(point.getEnvelopeInternal(), true);
		int minZoom = SegmentationHelper.getMinimumZoomLevel(mapNode,
				classLookup);
		intervalTreeNodes.getObject(minZoom).add(rect, mapNode);
	}

	private boolean isHousenumber(Map<String, String> tags)
	{
		return tags.containsKey("addr:housenumber");
	}

	private void housenumber(Point point, Map<String, String> tags)
	{
		String hn = tags.get("addr:housenumber");
		Coordinate coordinate = GeometryConverter.convert(point);
		TextNode housenumber = new TextNode(hn, coordinate);
		BoundingBox rect = new BoundingBox(point.getEnvelopeInternal(), true);
		treeHousenumbers.add(rect, housenumber);
	}

	@Override
	public void processWayString(OsmWay way, LineString string,
			Map<String, String> tags)
	{
		calculator.add(string.getCentroid());

		de.topobyte.mapocado.mapformat.geom.Linestring geom = GeometryConverter
				.convert(string);
		Map<Integer, String> itags = TagUtil.convertTags(tags, keepPool);
		Way mapWay = new Way(itags, geom);

		Set<ObjectClassRef> elements = ruleMatcher.getElements(mapWay, tags, -1,
				-1, true);

		if (isHousenumber(tags)) {
			Point point = string.getCentroid();
			housenumber(point, tags);
		}

		if (elements.size() == 0) {
			return;
		}

		/*
		 * separate object classes here. Those that simplify a way to a node
		 * generate another, different object.
		 */
		Set<ObjectClassRef> normalReferences = new HashSet<>();
		Set<ObjectClassRef> nodeReferences = new HashSet<>();

		for (ObjectClassRef classRef : elements) {
			switch (classRef.getSimplification()) {
			case NONE:
				normalReferences.add(classRef);
				break;
			case NODE:
				nodeReferences.add(classRef);
				break;
			}
		}
		// only include as a way if there has been at least one class of
		// type way.
		if (normalReferences.size() > 0) {
			mapWay.getClasses().add(
					ElementHelper.getReferenceIds(normalReferences, refPool));
			mapWay.eraseUnnecessaryTags(normalReferences, keepPool);

			BoundingBox rect = new BoundingBox(string.getEnvelopeInternal(),
					true);
			int minZoom = SegmentationHelper.getMinimumZoomLevel(mapWay,
					classLookup);
			intervalTreeWays.getObject(minZoom).add(rect, mapWay);
		}
		// only include as a node if there has been at least one class
		// of type node.
		if (nodeReferences.size() > 0) {
			Point point = string.getCentroid();
			Coordinate coord = GeometryConverter.convert(point);
			Node mapNode = new Node(itags, coord);
			mapNode.getClasses().add(
					ElementHelper.getReferenceIds(nodeReferences, refPool));
			mapNode.eraseUnnecessaryTags(nodeReferences, keepPool);

			BoundingBox rect = new BoundingBox(point.getEnvelopeInternal(),
					true);
			int minZoom = SegmentationHelper.getMinimumZoomLevel(mapNode,
					classLookup);
			intervalTreeNodes.getObject(minZoom).add(rect, mapNode);
		}
	}

	@Override
	public void processMultipolygon(OsmWay way, MultiPolygon polygon,
			Map<String, String> tags, Point centroid)
	{
		Multipolygon mercPolygon = GeometryConverter.convert(polygon);
		processMultipolygon(mercPolygon, polygon, tags, centroid);
	}

	@Override
	public void processMultipolygon(OsmRelation relation, MultiPolygon polygon,
			Map<String, String> tags, Point centroid)
	{
		Multipolygon mercPolygon = GeometryConverter.convert(polygon);
		processMultipolygon(mercPolygon, polygon, tags, centroid);
	}

	private void processPolygon(Polygon polygon, Map<String, String> tags)
	{
		Point centroid = polygon.getCentroid();
		MultiPolygon mp = polygon.getFactory()
				.createMultiPolygon(new Polygon[] { polygon });
		Multipolygon mercPolygon = GeometryConverter.convert(mp);
		processMultipolygon(mercPolygon, polygon, tags, centroid);
	}

	private void processMultiPolygon(MultiPolygon polygon,
			Map<String, String> tags)
	{
		Point centroid = polygon.getCentroid();
		Multipolygon mercPolygon = GeometryConverter.convert(polygon);
		processMultipolygon(mercPolygon, polygon, tags, centroid);
	}

	public void processMultipolygon(Multipolygon mercPolygon, Geometry geometry,
			Map<String, String> tags, Point centroid)
	{
		Map<Integer, String> itags = TagUtil.convertTags(tags, keepPool);
		Relation mapRelation = new Relation(itags, mercPolygon);

		Set<ObjectClassRef> elements = ruleMatcher.getElements(mapRelation,
				tags, -1, -1, true);

		if (isHousenumber(tags)) {
			housenumber(centroid, tags);
		}

		if (elements.size() == 0) {
			return;
		}

		if (hasTooManyPoints(geometry, tags)) {
			tags.remove("barrier");
			split(geometry, tags);
			return;
		}

		Set<ObjectClassRef> normalReferences = new HashSet<>();
		Set<ObjectClassRef> nodeReferences = new HashSet<>();

		for (ObjectClassRef classRef : elements) {
			switch (classRef.getSimplification()) {
			case NONE:
				normalReferences.add(classRef);
				break;
			case NODE:
				nodeReferences.add(classRef);
				break;
			}
		}

		if (normalReferences.size() != 0) {
			mapRelation.getClasses().add(
					ElementHelper.getReferenceIds(normalReferences, refPool));
			mapRelation.eraseUnnecessaryTags(normalReferences, keepPool);

			BoundingBox rect = new BoundingBox(geometry.getEnvelopeInternal(),
					true);
			int minZoom = SegmentationHelper.getMinimumZoomLevel(mapRelation,
					classLookup);
			intervalTreeRelations.getObject(minZoom).add(rect, mapRelation);
		}
		if (nodeReferences.size() != 0 && centroid != null) {
			Coordinate coord = GeometryConverter.convert(centroid);
			Node mapNode = new Node(itags, coord);
			mapNode.getClasses().add(
					ElementHelper.getReferenceIds(nodeReferences, refPool));
			mapNode.eraseUnnecessaryTags(nodeReferences, keepPool);

			BoundingBox rect = new BoundingBox(centroid.getEnvelopeInternal(),
					true);
			int minZoom = SegmentationHelper.getMinimumZoomLevel(mapNode,
					classLookup);
			intervalTreeNodes.getObject(minZoom).add(rect, mapNode);
		}
	}

	private boolean hasTooManyPoints(Geometry geometry,
			Map<String, String> tags)
	{
		if (geometry.getNumPoints() > pointLimit) {
			dump(geometry, tags);
		}
		return geometry.getNumPoints() > pointLimit;
	}

	private int dumpCounter = 1;

	private void dump(Geometry geometry, Map<String, String> tags)
	{
		try {
			String filename = "object" + (dumpCounter++) + ".smx";
			File file = new File(logsDir, filename);
			EntityFile ef = new EntityFile();
			ef.setGeometry(geometry);
			for (Entry<String, String> tag : tags.entrySet()) {
				ef.addTag(tag.getKey(), tag.getValue());
			}
			SmxFileWriter.write(ef, file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void split(Geometry geometry, Map<String, String> tags)
	{
		logger.info("SPLITTING: " + tags);
		List<Geometry> parts = PolygonSplitUtil.split(geometry, pointLimit,
				SplitMode.PCA);
		logger.info("Number of result geometries: " + parts.size());
		for (Geometry part : parts) {
			if (part instanceof Polygon) {
				processPolygon((Polygon) part, tags);
			} else if (part instanceof MultiPolygon) {
				processMultiPolygon((MultiPolygon) part, tags);
			} else {
				logger.info("Unexpected result: " + part.getClass());
			}
		}
	}

}
