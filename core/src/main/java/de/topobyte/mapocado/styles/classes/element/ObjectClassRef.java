package de.topobyte.mapocado.styles.classes.element;

import java.util.HashSet;
import java.util.Set;

import de.topobyte.mapocado.styles.rules.enums.Simplification;

public class ObjectClassRef
{

	private final String ref;
	private final Simplification simplification;

	private Set<String> mandatoryKeepKeys = new HashSet<>();
	private Set<String> optionalKeepKeys = new HashSet<>();

	private int minZoom;
	private int maxZoom;

	public ObjectClassRef(String ref, Simplification simplification)
	{
		this.ref = ref;
		this.simplification = simplification;
	}

	public String getRef()
	{
		return ref;
	}

	public Set<String> getMandatoryKeepKeys()
	{
		return mandatoryKeepKeys;
	}

	public Set<String> getOptionalKeepKeys()
	{
		return optionalKeepKeys;
	}

	public Simplification getSimplification()
	{
		return simplification;
	}

	public void setMinZoom(int minZoom)
	{
		this.minZoom = minZoom;
	}

	public void setMaxZoom(int maxZoom)
	{
		this.maxZoom = maxZoom;
	}

	public int getMinZoom()
	{
		return minZoom;
	}

	public int getMaxZoom()
	{
		return maxZoom;
	}

	@Override
	public String toString()
	{
		return "ObjectClassRef ref: '" + ref + "', simplify: "
				+ simplification.toString();
	}

}
