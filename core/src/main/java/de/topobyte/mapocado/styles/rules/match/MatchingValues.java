package de.topobyte.mapocado.styles.rules.match;

import java.util.Set;
import java.util.TreeSet;

public class MatchingValues
{

	private boolean hasWildcard = false;
	private Set<String> values = new TreeSet<>();

	public void add(String value)
	{
		values.add(value);
	}

	public Set<String> getValues()
	{
		return values;
	}

	public void setHasWildcard(boolean has)
	{
		hasWildcard = has;
	}

	public boolean hasWildcard()
	{
		return hasWildcard;
	}

}
