package de.topobyte.mapocado.android.style;

import java.io.File;
import java.util.List;

import de.topobyte.mapocado.android.rendering.StyleConversion;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.directory.StyleDirectory;

public class MapRenderConfig
{

	private int backgroundColor;
	private int mapOverlayInner;
	private int mapOverlayOuter;
	private int mapOverlayGpsInner;
	private int mapOverlayGpsOuter;
	private StyleDirectory style;

	public MapRenderConfig(StyleDirectory style)
	{
		this.backgroundColor = StyleConversion
				.getColor(style.getBackgroundColor());
		this.mapOverlayInner = StyleConversion
				.getColor(style.getMapOverlayInner());
		this.mapOverlayOuter = StyleConversion
				.getColor(style.getMapOverlayOuter());
		this.mapOverlayGpsInner = StyleConversion
				.getColor(style.getMapOverlayGpsInner());
		this.mapOverlayGpsOuter = StyleConversion
				.getColor(style.getMapOverlayGpsOuter());
		this.style = style;
	}

	public int getBackgroundColor()
	{
		return backgroundColor;
	}

	public int getOverlayInner()
	{
		return mapOverlayInner;
	}

	public int getOverlayOuter()
	{
		return mapOverlayOuter;
	}

	public int getOverlayGpsInner()
	{
		return mapOverlayGpsInner;
	}

	public int getOverlayGpsOuter()
	{
		return mapOverlayGpsOuter;
	}

	public List<ObjectClass> getObjectClasses()
	{
		return style.getObjectClasses();
	}

	public File getTexture(String patternName)
	{
		return style.getPattern(patternName);
	}

	public File getSymbol(String source)
	{
		return style.getSymbol(source);
	}

	public StyleDirectory getStyleDirectory()
	{
		return style;
	}

}