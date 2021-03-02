package de.topobyte.mapocado.styles.visualize.html;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import de.topobyte.chromaticity.ColorCode;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.HtmlBuilder;
import de.topobyte.jsoup.components.Body;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.Img;
import de.topobyte.mapocado.styles.bundled.Paths;
import de.topobyte.mapocado.styles.classes.element.Area;
import de.topobyte.mapocado.styles.classes.element.Caption;
import de.topobyte.mapocado.styles.classes.element.Circle;
import de.topobyte.mapocado.styles.classes.element.Line;
import de.topobyte.mapocado.styles.classes.element.ObjectClass;
import de.topobyte.mapocado.styles.classes.element.PathText;
import de.topobyte.mapocado.styles.classes.element.PngSymbol;
import de.topobyte.mapocado.styles.classes.element.RenderElement;

public class ClassFileCreator
{

	private HtmlBuilder builder;

	private final List<ObjectClass> objectClasses;

	public ClassFileCreator(List<ObjectClass> objectClasses)
	{
		this.objectClasses = objectClasses;

	}

	public void createClassesFile(File fileClasses)
			throws ParserConfigurationException, TransformerException,
			IOException
	{
		builder = new HtmlBuilder();
		Body body = builder.getBody();

		for (ObjectClass objectClass : objectClasses) {
			// meta info
			String metaLine = createObjectClassLine(builder, objectClass);
			body.append(metaLine);
			body.ac(HTML.br());

			// render styles
			RenderElement[] elements = objectClass.elements;
			for (RenderElement element : elements) {
				body.ac(createElementLine(builder, element));
				body.ac(HTML.br()).attr("style", "clear:both");
			}
		}

		builder.write(fileClasses);
	}

	public static String createObjectClassLine(HtmlBuilder builder,
			ObjectClass objectClass)
	{
		return String.format("id: %s, minZoom: %d, maxZoom: %d",
				objectClass.getId(), objectClass.getMinZoom(),
				objectClass.getMaxZoom());
	}

	public static Div createElementLine(HtmlBuilder builder,
			RenderElement element)
	{
		Div box = HTML.div();
		if (element instanceof Line) {
			Line line = (Line) element;
			String strokeValue = String.format("0x%X",
					line.getStroke().getValue());
			String elementText = String.format("Line Stroke: %s", strokeValue);
			box.append(elementText);
			box.ac(createColorBox(builder, line.getStroke()));
		} else if (element instanceof Area) {
			Area area = (Area) element;
			String strokeValue = area.getStroke() == null ? null
					: String.format("0x%X", area.getStroke().getValue());
			String fillValue = area.getFill() == null ? null
					: String.format("0x%X", area.getFill().getValue());
			String elementText = String.format("Area Stroke: %s, Fill: %s",
					strokeValue, fillValue);
			box.at(elementText);
			if (area.getStroke() != null) {
				box.ac(createColorBox(builder, area.getStroke()));
			}
			if (area.getFill() != null) {
				box.ac(createColorBox(builder, area.getFill()));
			}
		} else if (element instanceof Circle) {
			Circle circle = (Circle) element;
			String elementText = String.format(
					"Circle Stroke: 0x%X, Fill: 0x%X",
					circle.getStroke().getValue(), circle.getFill().getValue());
			box.at(elementText);
			box.ac(createColorBox(builder, circle.getStroke()));
			box.ac(createColorBox(builder, circle.getFill()));
		} else if (element instanceof PngSymbol) {
			PngSymbol symbol = (PngSymbol) element;
			String source = symbol.getSource();
			String elementText = String.format("Symbol source: %s", source);
			box.at(elementText);
			box.ac(createSymbolImage(builder, source));
		} else if (element instanceof Caption) {
			Caption caption = (Caption) element;
			String elementText = String.format("Caption key: %s",
					caption.getKey());
			box.at(elementText);
			box.ac(createColorBox(builder, caption.getStroke()));
			box.ac(createColorBox(builder, caption.getFill()));
		} else if (element instanceof PathText) {
			PathText pathText = (PathText) element;
			String elementText = String.format("PathText key: %s",
					pathText.getKey());
			box.at(elementText);
			box.ac(createColorBox(builder, pathText.getStroke()));
			box.ac(createColorBox(builder, pathText.getFill()));
		} else {
			box.at("" + element.getClass().getSimpleName());
		}

		return box;
	}

	private static Img createSymbolImage(HtmlBuilder builder, String source)
	{
		return HTML.img(getSymbolPath(builder, source)).attr("style",
				"float:left; margin-right:2px");
	}

	private static String getSymbolPath(HtmlBuilder builder, String source)
	{
		return Paths.DIR_SYMBOLS + "/" + source;
	}

	private static Div createColorBox(HtmlBuilder builder, ColorCode colorCode)
	{
		Div div = HTML.div();
		int value = colorCode.getValue() & 0xFFFFFF;
		String color = String.format("%X", value);
		div.attr("style", "float:left;width:30px;height:30px;background-color:#"
				+ color + ";margin-right:2px;border:1px solid black");
		return div;
	}
}
