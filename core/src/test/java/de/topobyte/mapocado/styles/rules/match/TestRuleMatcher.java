package de.topobyte.mapocado.styles.rules.match;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.mapformat.geom.Linestring;
import de.topobyte.mapocado.mapformat.io.Metadata;
import de.topobyte.mapocado.mapformat.io.StringPool;
import de.topobyte.mapocado.mapformat.model.Way;
import de.topobyte.mapocado.mapformat.util.TagUtil;
import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;
import de.topobyte.mapocado.styles.rules.RuleFileReader;
import de.topobyte.mapocado.styles.rules.RuleSet;
import de.topobyte.osm4j.core.model.iface.EntityContainer;
import de.topobyte.osm4j.core.model.iface.OsmEntity;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;
import de.topobyte.system.utils.SystemPaths;

public class TestRuleMatcher
{

	final static Logger logger = LoggerFactory.getLogger(TestRuleMatcher.class);

	public static void main(String[] args) throws FileNotFoundException
	{
		Path configDir = SystemPaths.HOME.resolve(
				"github/topobyte/mapocado/config/citymaps/rendertheme-v2/rules/default");

		RuleSet config = new RuleSet();
		try {
			File[] ruleFiles = configDir.toFile().listFiles();
			for (File ruleFile : ruleFiles) {
				if (!ruleFile.getName().endsWith(".xml")) {
					continue;
				}
				RuleFileReader.read(config, ruleFile.getPath());
			}
		} catch (Exception e) {
			logger.error("Exception while parsing rules: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		RuleMatcher matcher = new RuleMatcher(config);

		StringPool keepPool = Metadata
				.buildKeepKeyPool(config.getObjectClassRefs());

		String inputPath = "/tmp/lisbon/4247904.xml";

		OsmXmlIterator iterator = new OsmXmlIterator(inputPath, false);
		for (EntityContainer container : iterator) {
			OsmEntity entity = container.getEntity();
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(entity);

			Map<Integer, String> itags = TagUtil.convertTags(tags, keepPool);

			Linestring ls = new Linestring(new int[] { 0, 0 },
					new int[] { 0, 1, 2 });

			Way mapWay = new Way(itags, ls);
			Set<ObjectClassRef> elements = matcher.getElements(mapWay, tags, -1,
					-1, true);

			for (ObjectClassRef ref : elements) {
				System.out.println(ref);
			}
		}
	}
}
