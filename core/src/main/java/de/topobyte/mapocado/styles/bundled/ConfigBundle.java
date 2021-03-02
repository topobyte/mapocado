package de.topobyte.mapocado.styles.bundled;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import de.topobyte.mapocado.styles.MapStyleDataProvider;

public class ConfigBundle implements MapStyleDataProvider
{
	public static String NAME_CLASSES = "classes.xml";
	public static String NAME_LABELS = "labels.xml";
	public static String PREFIX_PATTERNS = "patterns/";
	public static String PREFIX_SYMBOLS = "symbols/";
	public static int LENGTH_PREFIX_PATTERNS = PREFIX_PATTERNS.length();
	public static int LENGTH_PREFIX_SYMBOLS = PREFIX_SYMBOLS.length();

	private final byte[] classes;
	private final byte[] labels;
	private final Map<String, byte[]> patterns;
	private final Map<String, byte[]> symbols;

	public ConfigBundle(byte[] classes, byte[] labels,
			Map<String, byte[]> patterns, Map<String, byte[]> symbols)
	{
		this.classes = classes;
		this.labels = labels;
		this.patterns = patterns;
		this.symbols = symbols;
	}

	public InputStream getClassesAsInputStream()
	{
		return new ByteArrayInputStream(classes);
	}

	public InputStream getLabelsAsInputStream()
	{
		return new ByteArrayInputStream(labels);
	}

	public boolean hasSymbol(String name)
	{
		return symbols.containsKey(name);
	}

	public Set<String> getSymbolNames()
	{
		return symbols.keySet();
	}

	public InputStream getSymbolAsInputStream(String name)
			throws FileNotFoundException
	{
		byte[] bytes = symbols.get(name);
		if (bytes == null) {
			throw new FileNotFoundException(name);
		}
		return new ByteArrayInputStream(bytes);
	}

	public boolean hasPattern(String name)
	{
		return patterns.containsKey(name);
	}

	public Set<String> getPatternNames()
	{
		return patterns.keySet();
	}

	public InputStream getPatternAsInputStream(String name)
			throws FileNotFoundException
	{
		byte[] bytes = patterns.get(name);
		if (bytes == null) {
			throw new FileNotFoundException(name);
		}
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public InputStream getSymbol(String name) throws IOException
	{
		return getSymbolAsInputStream(name);
	}

	@Override
	public InputStream getTexture(String name) throws IOException
	{
		return getPatternAsInputStream(name);
	}

}
