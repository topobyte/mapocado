package de.topobyte.mapocado.styles.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.mapocado.styles.classes.element.ObjectClassRef;

public class RuleSet
{
	final static Logger logger = LoggerFactory.getLogger(RuleSet.class);

	private List<Rule> rules = new ArrayList<>();

	private List<ObjectClassRef> objectClassRefs = new ArrayList<>();
	private Map<String, ObjectClassRef> classes = new HashMap<>();

	public List<Rule> getRules()
	{
		return rules;
	}

	public List<ObjectClassRef> getObjectClassRefs()
	{
		return objectClassRefs;
	}

	public void addRule(Rule rule)
	{
		rules.add(rule);
	}

	public boolean hasObjectClass(String ref)
	{
		return classes.containsKey(ref);
	}

	public ObjectClassRef getObjectClassRef(String ref)
	{
		return classes.get(ref);
	}

	public void addObjectClass(ObjectClassRef objectClassRef)
	{
		String ref = objectClassRef.getRef();
		if (classes.containsKey(ref)) {
			System.out.println(
					String.format("duplicate object class. ref: %s", ref));
			logger.warn(String.format("duplicate object class. ref: %s", ref));
			return;
		}
		objectClassRefs.add(objectClassRef);
		classes.put(ref, objectClassRef);
	}
}
